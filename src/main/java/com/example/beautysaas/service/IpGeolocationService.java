package com.example.beautysaas.service;

import com.example.beautysaas.entity.GeoLocation;
import com.example.beautysaas.repository.GeoLocationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpGeolocationService {

    private final GeoLocationRepository geoLocationRepository;
    private final RestTemplate restTemplate;
    private final SecurityMetricsService securityMetricsService;
    
    @Value("${security.geolocation.api-key}")
    private String apiKey;
    
    @Value("${security.geolocation.api-url}")
    private String apiUrl;
    
    @Value("${security.geolocation.cache-duration-hours:24}")
    private int cacheDurationHours;
    
    /**
     * Get geolocation data for an IP address
     */
    @Cacheable(value = "ipGeolocation", key = "#ipAddress")
    public Optional<GeoLocation> getIpGeolocation(String ipAddress) {
        // Check database cache first
        Optional<GeoLocation> cachedLocation = geoLocationRepository.findByIpAddress(ipAddress);
        if (cachedLocation.isPresent() && !isGeolocationExpired(cachedLocation.get())) {
            return cachedLocation;
        }
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(
                apiUrl + "?ip=" + ipAddress + "&key=" + apiKey,
                JsonNode.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                GeoLocation location = parseGeolocationResponse(response.getBody(), ipAddress);
                geoLocationRepository.save(location);
                return Optional.of(location);
            }
        } catch (Exception e) {
            log.error("Error fetching geolocation data for IP: {}", ipAddress, e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Check for suspicious location changes
     */
    public boolean isSuspiciousLocationChange(String email, String ipAddress) {
        Optional<GeoLocation> newLocation = getIpGeolocation(ipAddress);
        Optional<GeoLocation> lastLocation = geoLocationRepository.findLastLocationByEmail(email);
        
        if (newLocation.isPresent() && lastLocation.isPresent()) {
            GeoLocation current = newLocation.get();
            GeoLocation last = lastLocation.get();
            
            // Check if location change is suspicious based on time and distance
            if (isRapidLocationChange(last, current)) {
                log.warn("Suspicious rapid location change detected for email: {}", email);
                securityMetricsService.updateMetrics(LocalDateTime.now().minusMinutes(5), LocalDateTime.now());
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Parse geolocation API response
     */
    private GeoLocation parseGeolocationResponse(JsonNode response, String ipAddress) {
        return GeoLocation.builder()
                .ipAddress(ipAddress)
                .country(response.path("country").asText())
                .city(response.path("city").asText())
                .region(response.path("region").asText())
                .latitude(response.path("lat").asDouble())
                .longitude(response.path("lon").asDouble())
                .timezone(response.path("timezone").asText())
                .isp(response.path("isp").asText())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    /**
     * Check if cached geolocation data is expired
     */
    private boolean isGeolocationExpired(GeoLocation location) {
        return location.getLastUpdated()
                .plusHours(cacheDurationHours)
                .isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if location change is suspiciously rapid
     */
    private boolean isRapidLocationChange(GeoLocation last, GeoLocation current) {
        // Calculate time difference
        long hoursDifference = java.time.Duration.between(
            last.getLastUpdated(), 
            LocalDateTime.now()
        ).toHours();
        
        // Calculate distance between locations
        double distance = calculateDistance(
            last.getLatitude(), last.getLongitude(),
            current.getLatitude(), current.getLongitude()
        );
        
        // If locations are far apart and time difference is small, consider it suspicious
        return distance > 500 && hoursDifference < 2; // 500km in 2 hours
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}

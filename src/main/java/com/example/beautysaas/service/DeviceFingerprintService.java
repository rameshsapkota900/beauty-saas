package com.example.beautysaas.service;

import com.example.beautysaas.entity.DeviceFingerprint;
import com.example.beautysaas.repository.DeviceFingerprintRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceFingerprintService {
    
    private final DeviceFingerprintRepository deviceFingerprintRepository;
    private final SecurityEventNotifierService eventNotifier;
    private final Set<String> knownMaliciousFingerprints = ConcurrentHashMap.newKeySet();
    
    /**
     * Generate device fingerprint from request
     */
    public String generateFingerprint(HttpServletRequest request) {
        StringBuilder fingerprint = new StringBuilder();
        
        // Collect device information
        fingerprint.append(request.getHeader("User-Agent"))
                  .append(request.getHeader("Accept-Language"))
                  .append(request.getHeader("Accept-Encoding"))
                  .append(request.getHeader("Accept"))
                  .append(request.getHeader("Sec-Ch-Ua"))
                  .append(request.getHeader("Sec-Ch-Ua-Platform"))
                  .append(request.getHeader("Sec-Ch-Ua-Mobile"));
                  
        // Hash the fingerprint
        return hashFingerprint(fingerprint.toString());
    }
    
    /**
     * Record a device fingerprint for a user
     */
    @Transactional
    public void recordDeviceFingerprint(String email, String fingerprint, String ipAddress) {
        if (knownMaliciousFingerprints.contains(fingerprint)) {
            eventNotifier.notifySecurityEvent(
                "MALICIOUS_DEVICE",
                email,
                "Attempt to use known malicious device fingerprint"
            );
            return;
        }
        
        DeviceFingerprint device = deviceFingerprintRepository.findByEmailAndFingerprint(email, fingerprint)
                .orElse(DeviceFingerprint.builder()
                        .email(email)
                        .fingerprint(fingerprint)
                        .firstSeenIp(ipAddress)
                        .trustScore(0.5)
                        .build());
                        
        device.setLastSeenIp(ipAddress);
        device.setLastSeenAt(java.time.LocalDateTime.now());
        device.setUsageCount(device.getUsageCount() + 1);
        
        // Update trust score based on usage
        updateTrustScore(device);
        
        deviceFingerprintRepository.save(device);
    }
    
    /**
     * Check if device is trusted for a user
     */
    public boolean isDeviceTrusted(String email, String fingerprint) {
        if (knownMaliciousFingerprints.contains(fingerprint)) {
            return false;
        }
        
        return deviceFingerprintRepository.findByEmailAndFingerprint(email, fingerprint)
                .map(device -> device.getTrustScore() >= 0.7)
                .orElse(false);
    }
    
    /**
     * Mark a device as suspicious
     */
    @Transactional
    public void markDeviceSuspicious(String fingerprint, String reason) {
        deviceFingerprintRepository.findByFingerprint(fingerprint)
                .forEach(device -> {
                    device.setTrustScore(0.0);
                    deviceFingerprintRepository.save(device);
                    
                    eventNotifier.notifySecurityEvent(
                        "SUSPICIOUS_DEVICE",
                        device.getEmail(),
                        "Device marked as suspicious: " + reason
                    );
                });
                
        knownMaliciousFingerprints.add(fingerprint);
    }
    
    /**
     * Get user's trusted devices
     */
    public Set<DeviceFingerprint> getTrustedDevices(String email) {
        return deviceFingerprintRepository.findByEmailAndTrustScoreGreaterThanEqual(email, 0.7);
    }
    
    /**
     * Update device trust score
     */
    private void updateTrustScore(DeviceFingerprint device) {
        double currentScore = device.getTrustScore();
        int usageCount = device.getUsageCount();
        
        // Increase trust score with consistent usage
        if (usageCount > 10) {
            currentScore = Math.min(1.0, currentScore + 0.1);
        }
        
        // Decrease trust score if IP changes frequently
        if (!device.getLastSeenIp().equals(device.getFirstSeenIp())) {
            currentScore = Math.max(0.0, currentScore - 0.2);
        }
        
        device.setTrustScore(currentScore);
    }
    
    /**
     * Hash the fingerprint data
     */
    private String hashFingerprint(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error hashing fingerprint", e);
            return input;
        }
    }
}

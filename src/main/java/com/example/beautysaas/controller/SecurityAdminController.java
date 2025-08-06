package com.example.beautysaas.controller;

import com.example.beautysaas.service.SecurityReportGenerator;
import com.example.beautysaas.service.SecurityNotificationService;
import com.example.beautysaas.dto.security.SecurityReportDto;
import com.example.beautysaas.dto.security.SecurityNotificationDto;
import com.example.beautysaas.dto.security.SecurityIncidentDto;
import com.example.beautysaas.dto.security.ResourceAccessDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security")
@PreAuthorize("hasRole('SECURITY_ADMIN')")
@RequiredArgsConstructor
public class SecurityAdminController {
    private final SecurityReportGenerator reportGenerator;
    private final SecurityNotificationService notificationService;

    @GetMapping("/report")
    public ResponseEntity<SecurityReportDto> getSecurityReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Map<String, Object> report = reportGenerator.generateSecurityReport(startTime, endTime);
        return ResponseEntity.ok(mapToSecurityReport(report, startTime, endTime));
    }

    @GetMapping("/report/summary")
    public ResponseEntity<SecurityReportDto> getCurrentSummary() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(4);
        
        Map<String, Object> report = reportGenerator.generateSecurityReport(startTime, endTime);
        return ResponseEntity.ok(mapToSecurityReport(report, startTime, endTime));
    }

    @GetMapping("/report/daily")
    public ResponseEntity<SecurityReportDto> getDailyReport(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        
        LocalDateTime endTime = date != null ? date : LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(1);
        
        Map<String, Object> report = reportGenerator.generateSecurityReport(startTime, endTime);
        return ResponseEntity.ok(mapToSecurityReport(report, startTime, endTime));
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> sendSecurityNotification(@RequestBody SecurityNotificationDto notification) {
        notificationService.notifySecurityIncident(
            notification.getRecipient(),
            notification.getSubject(),
            notification.getDetails()
        );
        return ResponseEntity.ok().build();
    }
    
    private SecurityReportDto mapToSecurityReport(Map<String, Object> report, LocalDateTime startTime, LocalDateTime endTime) {
        SecurityReportDto dto = new SecurityReportDto();
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setTotalEvents((Long) report.get("totalEvents"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> incidents = (Map<String, Object>) report.get("securityIncidents");
        if (incidents != null) {
            dto.setSuspiciousEvents((Long) incidents.get("total"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> timeline = (List<Map<String, Object>>) incidents.get("timeline");
            if (timeline != null) {
                dto.setRecentIncidents(mapSecurityIncidents(timeline));
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resourceAccess = (Map<String, Object>) report.get("resourceAccessPatterns");
        if (resourceAccess != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> hotspots = (List<Map<String, Object>>) resourceAccess.get("hotspots");
            if (hotspots != null) {
                dto.setHotspots(mapResourceAccess(hotspots));
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> userActivity = (Map<String, Object>) report.get("userActivitySummary");
        if (userActivity != null) {
            dto.setUniqueUsers((Long) userActivity.get("activeUsers"));
        }
        
        // Map event types and severities
        @SuppressWarnings("unchecked")
        Map<String, Long> eventsByType = (Map<String, Long>) report.get("eventsByType");
        dto.setEventsByType(eventsByType);
        
        @SuppressWarnings("unchecked")
        Map<String, Long> eventsBySeverity = (Map<String, Long>) report.get("eventsBySeverity");
        dto.setEventsBySeverity(eventsBySeverity);
        
        return dto;
    }
    
    private List<SecurityIncidentDto> mapSecurityIncidents(List<Map<String, Object>> incidents) {
        return incidents.stream()
            .map(incident -> {
                SecurityIncidentDto dto = new SecurityIncidentDto();
                dto.setTimestamp((LocalDateTime) incident.get("timestamp"));
                dto.setEventType((String) incident.get("type"));
                dto.setUserId((String) incident.get("user"));
                dto.setDetails((String) incident.get("details"));
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<ResourceAccessDto> mapResourceAccess(List<Map<String, Object>> hotspots) {
        return hotspots.stream()
            .map(hotspot -> {
                ResourceAccessDto dto = new ResourceAccessDto();
                String resource = (String) hotspot.get("resource");
                String[] parts = resource.split(":");
                dto.setResourceType(parts[0]);
                dto.setResourceId(parts[1]);
                dto.setAccessCount((Long) hotspot.get("accessCount"));
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}

package com.example.beautysaas.service;

import com.example.beautysaas.entity.AuditTrail;
import com.example.beautysaas.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityReportGenerator {
    private final AuditTrailRepository auditTrailRepository;
    private final BehaviorAnalyzer behaviorAnalyzer;
    private final SessionAnalyzer sessionAnalyzer;

    public Map<String, Object> generateSecurityReport(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> report = new HashMap<>();
        
        // Add summary statistics
        report.put("totalEvents", calculateTotalEvents(startTime, endTime));
        report.put("securityIncidents", analyzeSecurityIncidents(startTime, endTime));
        report.put("userActivitySummary", analyzeUserActivity(startTime, endTime));
        report.put("suspiciousActivities", analyzeSuspiciousActivities(startTime, endTime));
        report.put("resourceAccessPatterns", analyzeResourceAccess(startTime, endTime));
        
        return report;
    }

    private long calculateTotalEvents(LocalDateTime startTime, LocalDateTime endTime) {
        return auditTrailRepository.countByCreatedAtBetween(startTime, endTime);
    }

    private Map<String, Object> analyzeSecurityIncidents(LocalDateTime startTime, LocalDateTime endTime) {
        List<AuditTrail> securityEvents = auditTrailRepository.findSecurityIncidents(startTime, endTime);
        
        Map<String, Object> incidents = new HashMap<>();
        incidents.put("total", securityEvents.size());
        incidents.put("byType", groupByEventType(securityEvents));
        incidents.put("bySeverity", groupBySeverity(securityEvents));
        incidents.put("timeline", createTimeline(securityEvents));
        
        return incidents;
    }

    private Map<String, Object> analyzeUserActivity(LocalDateTime startTime, LocalDateTime endTime) {
        List<AuditTrail> userEvents = auditTrailRepository.findUserActivity(startTime, endTime);
        
        Map<String, Object> userActivity = new HashMap<>();
        userActivity.put("activeUsers", countActiveUsers(userEvents));
        userActivity.put("activityByUser", groupByUser(userEvents));
        userActivity.put("unusualPatterns", detectUnusualPatterns(userEvents));
        
        return userActivity;
    }

    private List<Map<String, Object>> analyzeSuspiciousActivities(LocalDateTime startTime, LocalDateTime endTime) {
        List<AuditTrail> events = auditTrailRepository.findAll();
        
        return events.stream()
            .filter(event -> behaviorAnalyzer.isAnomalous(event.getEmail(), 
                event.getEventType().toString(), event.getCreatedAt()))
            .map(this::createSuspiciousActivityReport)
            .collect(Collectors.toList());
    }

    private Map<String, Object> analyzeResourceAccess(LocalDateTime startTime, LocalDateTime endTime) {
        List<AuditTrail> accessEvents = auditTrailRepository.findResourceAccess(startTime, endTime);
        
        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("byResource", groupByResource(accessEvents));
        resourceAccess.put("byAccessType", groupByAccessType(accessEvents));
        resourceAccess.put("hotspots", identifyAccessHotspots(accessEvents));
        
        return resourceAccess;
    }

    private Map<String, Long> groupByEventType(List<AuditTrail> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                event -> event.getEventType().toString(),
                Collectors.counting()
            ));
    }

    private Map<String, Long> groupBySeverity(List<AuditTrail> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                event -> event.getSeverity().toString(),
                Collectors.counting()
            ));
    }

    private List<Map<String, Object>> createTimeline(List<AuditTrail> events) {
        return events.stream()
            .map(event -> {
                Map<String, Object> timelineEntry = new HashMap<>();
                timelineEntry.put("timestamp", event.getCreatedAt());
                timelineEntry.put("type", event.getEventType());
                timelineEntry.put("user", event.getEmail());
                timelineEntry.put("details", event.getEventDetails());
                return timelineEntry;
            })
            .collect(Collectors.toList());
    }

    private long countActiveUsers(List<AuditTrail> events) {
        return events.stream()
            .map(AuditTrail::getEmail)
            .distinct()
            .count();
    }

    private Map<String, Long> groupByUser(List<AuditTrail> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                AuditTrail::getEmail,
                Collectors.counting()
            ));
    }

    private List<Map<String, Object>> detectUnusualPatterns(List<AuditTrail> events) {
        return events.stream()
            .collect(Collectors.groupingBy(AuditTrail::getEmail))
            .entrySet()
            .stream()
            .map(entry -> {
                Map<String, Object> pattern = new HashMap<>();
                pattern.put("user", entry.getKey());
                pattern.put("totalActions", entry.getValue().size());
                pattern.put("unusualActionCount", countUnusualActions(entry.getValue()));
                pattern.put("riskScore", calculateUserRiskScore(entry.getValue()));
                return pattern;
            })
            .filter(pattern -> (double)pattern.get("riskScore") > 0.7)
            .collect(Collectors.toList());
    }

    private Map<String, Long> groupByResource(List<AuditTrail> events) {
        return events.stream()
            .filter(event -> event.getResourceType() != null)
            .collect(Collectors.groupingBy(
                AuditTrail::getResourceType,
                Collectors.counting()
            ));
    }

    private Map<String, Long> groupByAccessType(List<AuditTrail> events) {
        return events.stream()
            .filter(event -> event.getActionType() != null)
            .collect(Collectors.groupingBy(
                event -> event.getActionType().toString(),
                Collectors.counting()
            ));
    }

    private List<Map<String, Object>> identifyAccessHotspots(List<AuditTrail> events) {
        Map<String, Long> accessCounts = events.stream()
            .filter(event -> event.getResourceId() != null)
            .collect(Collectors.groupingBy(
                event -> event.getResourceType() + ":" + event.getResourceId(),
                Collectors.counting()
            ));

        return accessCounts.entrySet().stream()
            .filter(entry -> entry.getValue() > 100) // Threshold for hotspot
            .map(entry -> {
                Map<String, Object> hotspot = new HashMap<>();
                hotspot.put("resource", entry.getKey());
                hotspot.put("accessCount", entry.getValue());
                return hotspot;
            })
            .collect(Collectors.toList());
    }

    private Map<String, Object> createSuspiciousActivityReport(AuditTrail event) {
        Map<String, Object> report = new HashMap<>();
        report.put("timestamp", event.getCreatedAt());
        report.put("user", event.getEmail());
        report.put("eventType", event.getEventType());
        report.put("severity", event.getSeverity());
        report.put("riskScore", behaviorAnalyzer.calculateRiskScore(
            event.getEmail(), 
            event.getEventType().toString(), 
            event.getCreatedAt()
        ));
        return report;
    }

    private int countUnusualActions(List<AuditTrail> userEvents) {
        return (int) userEvents.stream()
            .filter(event -> behaviorAnalyzer.isAnomalous(
                event.getEmail(),
                event.getEventType().toString(),
                event.getCreatedAt()
            ))
            .count();
    }

    private double calculateUserRiskScore(List<AuditTrail> userEvents) {
        return userEvents.stream()
            .mapToDouble(event -> behaviorAnalyzer.calculateRiskScore(
                event.getEmail(),
                event.getEventType().toString(),
                event.getCreatedAt()
            ))
            .average()
            .orElse(0.0);
    }
}

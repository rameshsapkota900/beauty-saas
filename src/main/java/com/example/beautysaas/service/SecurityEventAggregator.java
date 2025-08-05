package com.example.beautysaas.service;

import com.example.beautysaas.entity.SecurityAuditLog;
import com.example.beautysaas.repository.SecurityAuditLogRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityEventAggregator {
    
    private final SecurityAuditLogRepository auditLogRepository;
    private final SecurityEventNotifierService eventNotifier;
    
    private final Map<String, EventMetrics> eventMetrics = new ConcurrentHashMap<>();
    
    @Data
    @Builder
    private static class EventMetrics {
        private int totalCount;
        private int failureCount;
        private int successCount;
        private Set<String> uniqueUsers;
        private Set<String> uniqueIps;
        private LocalDateTime firstSeen;
        private LocalDateTime lastSeen;
    }
    
    /**
     * Aggregate security events for analysis
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional(readOnly = true)
    public void aggregateEvents() {
        LocalDateTime from = LocalDateTime.now().minusMinutes(5);
        List<SecurityAuditLog> recentLogs = auditLogRepository.findByCreatedAtAfter(from);
        
        Map<String, EventMetrics.EventMetricsBuilder> builders = new HashMap<>();
        
        for (SecurityAuditLog log : recentLogs) {
            builders.computeIfAbsent(log.getEventType(), k -> EventMetrics.builder()
                    .totalCount(0)
                    .failureCount(0)
                    .successCount(0)
                    .uniqueUsers(new HashSet<>())
                    .uniqueIps(new HashSet<>())
                    .firstSeen(log.getCreatedAt())
                    .lastSeen(log.getCreatedAt()))
                .totalCount(builders.get(log.getEventType()).totalCount + 1)
                .failureCount(builders.get(log.getEventType()).failureCount + (log.getSuccess() ? 0 : 1))
                .successCount(builders.get(log.getEventType()).successCount + (log.getSuccess() ? 1 : 0))
                .uniqueUsers(builders.get(log.getEventType()).uniqueUsers.add(log.getEmail()))
                .uniqueIps(log.getIpAddress() != null ? builders.get(log.getEventType()).uniqueIps.add(log.getIpAddress()) : null)
                .lastSeen(log.getCreatedAt());
        }
        
        // Update metrics
        builders.forEach((eventType, builder) -> {
            eventMetrics.put(eventType, builder.build());
            analyzeEventPattern(eventType, builder.build());
        });
    }
    
    /**
     * Analyze event patterns for anomalies
     */
    private void analyzeEventPattern(String eventType, EventMetrics metrics) {
        // Check for high failure rates
        if (metrics.getTotalCount() > 10 && 
            (double) metrics.getFailureCount() / metrics.getTotalCount() > 0.7) {
            eventNotifier.notifySecurityEvent(
                "HIGH_FAILURE_RATE",
                "SYSTEM",
                String.format("High failure rate detected for event type %s: %.2f%%",
                    eventType, (double) metrics.getFailureCount() / metrics.getTotalCount() * 100)
            );
        }
        
        // Check for unusual activity spikes
        if (metrics.getTotalCount() > 100 && 
            metrics.getLastSeen().minusMinutes(5).isBefore(metrics.getFirstSeen())) {
            eventNotifier.notifySecurityEvent(
                "ACTIVITY_SPIKE",
                "SYSTEM",
                String.format("Unusual activity spike detected for event type %s: %d events in 5 minutes",
                    eventType, metrics.getTotalCount())
            );
        }
        
        // Check for distributed attacks
        if (metrics.getUniqueIps().size() > 20 && metrics.getFailureCount() > 50) {
            eventNotifier.notifySecurityEvent(
                "DISTRIBUTED_ATTACK",
                "SYSTEM",
                String.format("Possible distributed attack detected for event type %s: %d unique IPs",
                    eventType, metrics.getUniqueIps().size())
            );
        }
    }
    
    /**
     * Get event metrics for a specific event type
     */
    public EventMetrics getEventMetrics(String eventType) {
        return eventMetrics.get(eventType);
    }
    
    /**
     * Get summary of all event metrics
     */
    public Map<String, EventMetrics> getAllEventMetrics() {
        return new HashMap<>(eventMetrics);
    }
    
    /**
     * Get top security events by frequency
     */
    public List<Map.Entry<String, Integer>> getTopEvents(int limit) {
        return eventMetrics.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getTotalCount()))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Clear old metrics
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void clearOldMetrics() {
        eventMetrics.clear();
        log.info("Cleared security event metrics");
    }
}

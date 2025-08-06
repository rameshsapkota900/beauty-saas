package com.example.beautysaas.service;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Arrays;

@Component
public class BehaviorAnalyzer {
    private static final List<LocalTime> SUSPICIOUS_HOURS = Arrays.asList(
        LocalTime.of(1, 0),
        LocalTime.of(2, 0),
        LocalTime.of(3, 0),
        LocalTime.of(4, 0)
    );
    
    private static final List<String> SENSITIVE_ACTIONS = Arrays.asList(
        "DELETE",
        "MODIFY_PERMISSIONS",
        "BULK_DELETE",
        "EXPORT_DATA"
    );

    public double calculateRiskScore(String userId, String action, LocalDateTime timestamp) {
        double score = 0.0;
        
        // Check if action is performed during suspicious hours
        LocalTime time = timestamp.toLocalTime();
        if (SUSPICIOUS_HOURS.stream().anyMatch(hour -> 
            time.isAfter(hour) && time.isBefore(hour.plusHours(1)))) {
            score += 0.4;
        }
        
        // Check if action is sensitive
        if (SENSITIVE_ACTIONS.contains(action)) {
            score += 0.3;
        }
        
        return score;
    }

    public boolean isAnomalous(String userId, String action, LocalDateTime timestamp) {
        return calculateRiskScore(userId, action, timestamp) > 0.6;
    }
}

package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityEventNotifierService {
    
    @Value("${security.notification.admin-email}")
    private String adminEmail;
    
    private final Map<String, Integer> eventCountMap = new ConcurrentHashMap<>();
    private final NotificationService notificationService;
    
    public void notifySecurityEvent(String eventType, String userEmail, String details) {
        // Record event count
        int count = eventCountMap.compute(eventType + "_" + userEmail, (k, v) -> v == null ? 1 : v + 1);
        
        // Log the security event
        log.warn("Security event: {} for user: {}. Details: {}", eventType, userEmail, details);
        
        // Check if notification threshold is reached
        if (shouldNotifyAdmin(count, eventType)) {
            String message = String.format("Multiple security events (%s) detected for user: %s. Latest details: %s",
                                        eventType, userEmail, details);
            notifyAdmin(message);
        }
        
        // Check for critical events that need immediate notification
        if (isCriticalEvent(eventType)) {
            String message = String.format("Critical security event (%s) detected for user: %s. Details: %s",
                                        eventType, userEmail, details);
            notifyAdmin(message);
        }
    }
    
    private boolean shouldNotifyAdmin(int count, String eventType) {
        switch (eventType) {
            case "FAILED_LOGIN":
                return count >= 5;
            case "PASSWORD_RESET":
                return count >= 3;
            case "SUSPICIOUS_ACTIVITY":
                return count >= 2;
            default:
                return false;
        }
    }
    
    private boolean isCriticalEvent(String eventType) {
        return eventType.equals("BRUTE_FORCE_ATTEMPT") ||
               eventType.equals("UNAUTHORIZED_ACCESS") ||
               eventType.equals("ADMIN_ROLE_CHANGE");
    }
    
    private void notifyAdmin(String message) {
        try {
            notificationService.sendEmail(adminEmail, "Security Alert", message);
            log.info("Security notification sent to admin");
        } catch (Exception e) {
            log.error("Failed to send security notification to admin", e);
        }
    }
    
    // Cleanup old event counts periodically
    public void cleanupOldEvents() {
        eventCountMap.clear();
        log.info("Cleaned up security event counts");
    }
}

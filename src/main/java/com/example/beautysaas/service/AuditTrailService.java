package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditTrailService {
    private final SecurityAuditLogRepository securityAuditLogRepository;
    private final Map<String, Integer> actionFrequencyMap = new ConcurrentHashMap<>();
    
    @Transactional
    public void logAction(String userId, String action, String targetEntity, String details) {
        // Record action frequency
        String key = userId + ":" + action;
        actionFrequencyMap.compute(key, (k, v) -> v == null ? 1 : v + 1);
        
        // Create audit log entry
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
            .email(userId)
            .eventType(action)
            .details(String.format("Action: %s, Target: %s, Details: %s",
                                 action, targetEntity, details))
            .timestamp(LocalDateTime.now())
            .success(true)
            .build();
        
        securityAuditLogRepository.save(auditLog);
        
        // Check for suspicious activity
        checkActionFrequency(userId, action);
    }
    
    private void checkActionFrequency(String userId, String action) {
        String key = userId + ":" + action;
        int frequency = actionFrequencyMap.getOrDefault(key, 0);
        
        if (isHighFrequencyAction(action, frequency)) {
            log.warn("High frequency action detected - User: {}, Action: {}, Frequency: {}",
                    userId, action, frequency);
            
            SecurityAuditLog warningLog = SecurityAuditLog.builder()
                .email(userId)
                .eventType("HIGH_FREQUENCY_WARNING")
                .details(String.format("High frequency of action detected: %s (Count: %d)",
                                     action, frequency))
                .timestamp(LocalDateTime.now())
                .success(false)
                .build();
            
            securityAuditLogRepository.save(warningLog);
        }
    }
    
    private boolean isHighFrequencyAction(String action, int frequency) {
        switch (action) {
            case "LOGIN_ATTEMPT":
                return frequency > 10; // More than 10 login attempts
            case "PASSWORD_CHANGE":
                return frequency > 3;  // More than 3 password changes
            case "DATA_EXPORT":
                return frequency > 5;  // More than 5 data exports
            default:
                return frequency > 20; // Default threshold
        }
    }
    
    public void clearActionFrequency(String userId) {
        actionFrequencyMap.entrySet().removeIf(entry -> entry.getKey().startsWith(userId + ":"));
        log.info("Cleared action frequency for user: {}", userId);
    }
    
    @Transactional
    public void logSystemEvent(String eventType, String details) {
        SecurityAuditLog systemLog = SecurityAuditLog.builder()
            .email("SYSTEM")
            .eventType(eventType)
            .details(details)
            .timestamp(LocalDateTime.now())
            .success(true)
            .build();
        
        securityAuditLogRepository.save(systemLog);
    }
}

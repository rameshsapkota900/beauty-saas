package com.example.beautysaas.service;

import com.example.beautysaas.entity.AuditTrail;
import com.example.beautysaas.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditTrailService {
    private final AuditTrailRepository auditTrailRepository;
    private final ActionFrequencyMonitor actionFrequencyMonitor;
    private final SecurityViolationTracker securityViolationTracker;
    
    @Async
    @Transactional
    public void logAction(String userId, String action, String targetEntity, String details) {
        // Check if user is locked out
        if (securityViolationTracker.isLockedOut(userId)) {
            throw new SecurityException("Account is temporarily locked due to security violations");
        }

        // Record and check action frequency
        actionFrequencyMonitor.recordAction(userId, action);
        if (actionFrequencyMonitor.isFrequencyExceeded(userId, action)) {
            securityViolationTracker.recordViolation(userId);
            log.warn("Suspicious activity detected for user: {}, action: {}", userId, action);
        }
        
        // Create audit log entry
        AuditTrail auditLog = AuditTrail.builder()
            .email(userId)
            .eventType(AuditTrail.AuditEventType.valueOf(action))
            .eventDetails(String.format("Action: %s, Target: %s, Details: %s",
                                 action, targetEntity, details))
            .severity(AuditTrail.EventSeverity.INFO)
            .status(AuditTrail.EventStatus.SUCCESS)
            .createdAt(LocalDateTime.now())
            .build();
        
        auditTrailRepository.save(auditLog);
        
        // Check for suspicious activity
        checkActionFrequency(userId, action);
    }
    
    private void checkActionFrequency(String userId, String action) {
        String key = userId + ":" + action;
        int frequency = actionFrequencyMap.getOrDefault(key, 0);
        
        if (isHighFrequencyAction(action, frequency)) {
            log.warn("High frequency action detected - User: {}, Action: {}, Frequency: {}",
                    userId, action, frequency);
            
            AuditTrail warningLog = AuditTrail.builder()
                .email(userId)
                .eventType(AuditTrail.AuditEventType.SECURITY_WARNING)
                .eventDetails(String.format("High frequency of action detected: %s (Count: %d)",
                                     action, frequency))
                .severity(AuditTrail.EventSeverity.WARNING)
                .status(AuditTrail.EventStatus.FAILURE)
                .createdAt(LocalDateTime.now())
                .build();
            
            auditTrailRepository.save(warningLog);
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
    
    @Async
    @Transactional
    public void logSystemEvent(String eventType, String details) {
        AuditTrail systemLog = AuditTrail.builder()
            .email("SYSTEM")
            .eventType(AuditTrail.AuditEventType.valueOf(eventType))
            .eventDetails(details)
            .severity(AuditTrail.EventSeverity.INFO)
            .status(AuditTrail.EventStatus.SUCCESS)
            .createdAt(LocalDateTime.now())
            .build();
        
        auditTrailRepository.save(systemLog);
    }

    @Transactional(readOnly = true)
    public Page<AuditTrail> getUserAuditTrails(String email, Pageable pageable) {
        return auditTrailRepository.findByEmailOrderByCreatedAtDesc(email, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditTrail> getEventTypeAuditTrails(AuditTrail.AuditEventType eventType, Pageable pageable) {
        return auditTrailRepository.findByEventTypeOrderByCreatedAtDesc(eventType, pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditTrail> getSecurityIncidentsSince(LocalDateTime since) {
        return auditTrailRepository.findRecentSecurityIncidents(since);
    }

    @Transactional(readOnly = true)
    public List<String> getUsersWithRecentEventType(
            AuditTrail.AuditEventType eventType,
            AuditTrail.EventStatus status,
            LocalDateTime since) {
        return auditTrailRepository.findUsersWithRecentEventType(eventType, status, since);
    }

    @Transactional(readOnly = true)
    public boolean hasUserRecentEvent(
            String email,
            AuditTrail.AuditEventType eventType,
            AuditTrail.EventStatus status,
            LocalDateTime since) {
        return auditTrailRepository.hasRecentEvent(email, eventType, status, since);
    }

    @Transactional(readOnly = true)
    public List<AuditTrail> getUserEventsByTypeInPeriod(
            String email,
            AuditTrail.AuditEventType eventType,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return auditTrailRepository.findByEmailAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                email, eventType, startTime, endTime);
    }

    @Transactional(readOnly = true)
    public Page<AuditTrail> searchAuditTrails(String searchQuery, Pageable pageable) {
        return auditTrailRepository.searchAuditTrails(searchQuery, pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditTrail> getRecentEventsByTypeAndStatus(
            String email,
            AuditTrail.AuditEventType eventType,
            AuditTrail.EventStatus status,
            LocalDateTime since) {
        return auditTrailRepository.findRecentEventsByTypeAndStatus(email, eventType, status, since);
    }
}

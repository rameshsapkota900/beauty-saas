package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionManagementService {
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    private final SecurityEventNotifierService securityEventNotifier;
    
    private static class UserSession {
        final String userId;
        final String ipAddress;
        final LocalDateTime startTime;
        LocalDateTime lastActivityTime;
        
        UserSession(String userId, String ipAddress) {
            this.userId = userId;
            this.ipAddress = ipAddress;
            this.startTime = LocalDateTime.now();
            this.lastActivityTime = LocalDateTime.now();
        }
        
        void updateActivity() {
            this.lastActivityTime = LocalDateTime.now();
        }
        
        boolean isExpired(int timeoutMinutes) {
            return lastActivityTime.plusMinutes(timeoutMinutes).isBefore(LocalDateTime.now());
        }
    }
    
    public void createSession(String sessionId, String userId, String ipAddress) {
        activeSessions.put(sessionId, new UserSession(userId, ipAddress));
        log.info("Created new session for user: {}", userId);
    }
    
    public boolean validateSession(String sessionId, String userId, String ipAddress) {
        UserSession session = activeSessions.get(sessionId);
        
        if (session == null) {
            log.warn("Session not found: {}", sessionId);
            return false;
        }
        
        if (!session.userId.equals(userId)) {
            securityEventNotifier.notifySecurityEvent(
                "SESSION_USER_MISMATCH",
                userId,
                "Session user mismatch detected"
            );
            return false;
        }
        
        if (!session.ipAddress.equals(ipAddress)) {
            securityEventNotifier.notifySecurityEvent(
                "SESSION_IP_CHANGE",
                userId,
                "IP address change detected"
            );
            return false;
        }
        
        if (session.isExpired(30)) { // 30 minutes timeout
            invalidateSession(sessionId);
            return false;
        }
        
        session.updateActivity();
        return true;
    }
    
    public void invalidateSession(String sessionId) {
        UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            log.info("Invalidated session for user: {}", session.userId);
        }
    }
    
    public void invalidateUserSessions(String userId) {
        activeSessions.entrySet().removeIf(entry -> {
            if (entry.getValue().userId.equals(userId)) {
                log.info("Invalidated session {} for user: {}", entry.getKey(), userId);
                return true;
            }
            return false;
        });
    }
    
    public Map<String, String> getActiveSessions(String userId) {
        Map<String, String> userSessions = new ConcurrentHashMap<>();
        activeSessions.forEach((sessionId, session) -> {
            if (session.userId.equals(userId)) {
                userSessions.put(sessionId, session.ipAddress);
            }
        });
        return userSessions;
    }
    
    public void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired(30)) {
                log.info("Removed expired session for user: {}", entry.getValue().userId);
                return true;
            }
            return false;
        });
    }
}

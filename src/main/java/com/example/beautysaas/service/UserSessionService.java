package com.example.beautysaas.service;

import com.example.beautysaas.entity.UserSession;
import com.example.beautysaas.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {

    private final UserSessionRepository sessionRepository;
    private final SecurityService securityService;
    private final IpGeolocationService geoLocationService;
    
    @Value("${security.session.max-concurrent-sessions:3}")
    private int maxConcurrentSessions;
    
    @Value("${security.session.inactivity-timeout-minutes:30}")
    private int sessionInactivityTimeout;
    
    /**
     * Create a new user session
     */
    @Transactional
    public UserSession createSession(String email, String ipAddress, String userAgent) {
        // Generate unique session ID
        String sessionId = UUID.randomUUID().toString();
        
        // Create new session
        UserSession session = UserSession.builder()
                .sessionId(sessionId)
                .email(email)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        
        // Add geolocation data if available
        geoLocationService.getIpGeolocation(ipAddress)
                .ifPresent(session::setGeoLocation);
        
        // Check and enforce concurrent session limit
        LocalDateTime activeThreshold = LocalDateTime.now().minusMinutes(sessionInactivityTimeout);
        long activeSessions = sessionRepository.countActiveSessions(email, activeThreshold);
        
        if (activeSessions >= maxConcurrentSessions) {
            // Log security event for concurrent session limit
            securityService.logSecurityEvent(email, "CONCURRENT_SESSION_LIMIT", ipAddress, userAgent,
                    "Maximum concurrent session limit reached", false);
            
            // Deactivate oldest sessions
            sessionRepository.deactivateOtherSessions(email, sessionId, 
                    "Session terminated due to concurrent session limit");
        }
        
        sessionRepository.save(session);
        return session;
    }
    
    /**
     * Validate and update session
     */
    @Transactional
    public boolean validateSession(String sessionId, String ipAddress) {
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionIdAndIsActiveTrue(sessionId);
        
        if (sessionOpt.isEmpty()) {
            return false;
        }
        
        UserSession session = sessionOpt.get();
        
        // Check if session is expired
        if (isSessionExpired(session)) {
            session.markAsInactive("Session expired");
            sessionRepository.save(session);
            return false;
        }
        
        // Check for IP address change
        if (!session.getIpAddress().equals(ipAddress)) {
            securityService.logSecurityEvent(session.getEmail(), "SESSION_IP_MISMATCH", ipAddress,
                    session.getUserAgent(), "Session IP address mismatch detected", false);
            session.markAsInactive("IP address mismatch");
            sessionRepository.save(session);
            return false;
        }
        
        // Update last activity
        session.updateLastActivity();
        sessionRepository.save(session);
        return true;
    }
    
    /**
     * Terminate a specific session
     */
    @Transactional
    public void terminateSession(String sessionId, String reason) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.markAsInactive(reason);
            sessionRepository.save(session);
            
            securityService.logSecurityEvent(session.getEmail(), "SESSION_TERMINATED", 
                    session.getIpAddress(), session.getUserAgent(), 
                    "Session terminated: " + reason, true);
        });
    }
    
    /**
     * Terminate all sessions for a user except the current one
     */
    @Transactional
    public void terminateOtherSessions(String email, String currentSessionId) {
        sessionRepository.deactivateOtherSessions(email, currentSessionId, 
                "Terminated by user request");
        
        securityService.logSecurityEvent(email, "SESSIONS_TERMINATED", null, null,
                "All other sessions terminated by user request", true);
    }
    
    /**
     * Get active sessions for a user
     */
    public List<UserSession> getActiveSessions(String email) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(sessionInactivityTimeout);
        return sessionRepository.findActiveSessions(email, threshold);
    }
    
    /**
     * Check if session is expired
     */
    private boolean isSessionExpired(UserSession session) {
        LocalDateTime now = LocalDateTime.now();
        return session.getExpiresAt().isBefore(now) ||
               session.getLastActivity().plusMinutes(sessionInactivityTimeout).isBefore(now);
    }
    
    /**
     * Clean up expired sessions
     */
    @Scheduled(fixedRate = 900000) // Every 15 minutes
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(sessionInactivityTimeout);
        sessionRepository.deactivateExpiredSessions(threshold);
        log.debug("Cleaned up expired sessions");
    }
}

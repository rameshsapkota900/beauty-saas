package com.example.beautysaas.service;

import com.example.beautysaas.entity.AccountLockout;
import com.example.beautysaas.entity.SecurityAuditLog;
import com.example.beautysaas.repository.AccountLockoutRepository;
import com.example.beautysaas.repository.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final AccountLockoutRepository accountLockoutRepository;
    private final SecurityAuditLogRepository securityAuditLogRepository;
    private final RateLimitService rateLimitService;
    private final SecurityMetricsService securityMetricsService;
    private final Map<String, List<LoginAttempt>> ipLoginAttempts = new ConcurrentHashMap<>();

    @Value("${security.account-lockout.max-attempts:5}")
    private int maxFailedAttempts;
    
    @Value("${security.ip-tracking.suspicious-threshold:3}")
    private int suspiciousIpThreshold;
    
    private static class LoginAttempt {
        final String email;
        final LocalDateTime timestamp;
        final boolean success;
        
        LoginAttempt(String email, boolean success) {
            this.email = email;
            this.timestamp = LocalDateTime.now();
            this.success = success;
        }
    }

    @Value("${security.account-lockout.lockout-duration-minutes:30}")
    private int lockoutDurationMinutes;
    
    @Value("${security.rate-limit.window-minutes:15}")
    private int rateLimitWindowMinutes;

    @Value("${security.rate-limit.max-requests:100}")
    private int maxRequestsPerWindow;

    /**
     * Check if account is locked
     */
    public boolean isAccountLocked(String email) {
        Optional<AccountLockout> lockout = accountLockoutRepository.findActiveLockedAccount(email, LocalDateTime.now());
        return lockout.isPresent();
    }

    /**
     * Record failed login attempt and lock account if necessary
     */
    @Transactional
    public void recordFailedLoginAttempt(String email, String ipAddress, String userAgent) {
        // Log security event and update metrics
        logSecurityEvent(email, "LOGIN_FAILURE", ipAddress, userAgent, "Failed login attempt", false);
        securityMetricsService.updateMetrics(LocalDateTime.now().minusMinutes(5), LocalDateTime.now());

        AccountLockout lockout = accountLockoutRepository.findByEmail(email)
                .orElse(AccountLockout.builder()
                        .email(email)
                        .failedAttempts(0)
                        .isLocked(false)
                        .build());

        lockout.setFailedAttempts(lockout.getFailedAttempts() + 1);
        lockout.setLastFailedAttempt(LocalDateTime.now());

        if (lockout.getFailedAttempts() >= maxFailedAttempts) {
            lockout.setIsLocked(true);
            lockout.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutDurationMinutes));
            
            // Log account lockout event
            logSecurityEvent(email, "ACCOUNT_LOCKED", ipAddress, userAgent, 
                    "Account locked due to " + maxFailedAttempts + " failed attempts", false);
            
            log.warn("Account locked for email: {} due to {} failed attempts", email, lockout.getFailedAttempts());
        }

        accountLockoutRepository.save(lockout);
    }

    /**
     * Record successful login and reset failed attempts
     */
    @Transactional
    public void recordSuccessfulLogin(String email, String ipAddress, String userAgent, String sessionId) {
        // Reset failed attempts
        accountLockoutRepository.findByEmail(email).ifPresent(lockout -> {
            lockout.setFailedAttempts(0);
            lockout.setIsLocked(false);
            lockout.setLockedUntil(null);
            accountLockoutRepository.save(lockout);
        });

        // Log successful login
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .email(email)
                .eventType("LOGIN_SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .sessionId(sessionId)
                .details("Successful login")
                .success(true)
                .build();
        
        securityAuditLogRepository.save(auditLog);
        log.info("Successful login recorded for email: {}", email);
    }

    /**
     * Log security events
     */
    public void logSecurityEvent(String email, String eventType, String ipAddress, String userAgent, String details, boolean success) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .email(email)
                .eventType(eventType)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .details(details)
                .success(success)
                .build();
        
        securityAuditLogRepository.save(auditLog);
        log.info("Security event logged: {} for email: {}", eventType, email);
    }

    /**
     * Manually unlock account (admin function)
     */
    @Transactional
    public void unlockAccount(String email, String adminEmail) {
        accountLockoutRepository.unlockAccount(email);
        logSecurityEvent(email, "ACCOUNT_UNLOCKED", null, null, 
                "Account manually unlocked by admin: " + adminEmail, true);
        log.info("Account unlocked for email: {} by admin: {}", email, adminEmail);
    }

    /**
     * Clean up expired lockouts (scheduled task)
     */
    @Transactional
    public void unlockExpiredAccounts() {
        accountLockoutRepository.unlockExpiredAccounts(LocalDateTime.now());
        log.debug("Expired account lockouts cleaned up");
    }

    /**
     * Track IP-based login attempts and detect suspicious activity
     */
    private void trackIpLoginAttempt(String ipAddress, String email, boolean success) {
        ipLoginAttempts.computeIfAbsent(ipAddress, k -> new ArrayList<>())
                      .add(new LoginAttempt(email, success));
        
        // Check for suspicious activity
        List<LoginAttempt> attempts = ipLoginAttempts.get(ipAddress);
        long recentFailures = attempts.stream()
                .filter(attempt -> attempt.timestamp.isAfter(LocalDateTime.now().minusMinutes(30)))
                .filter(attempt -> !attempt.success)
                .count();
        
        if (recentFailures >= suspiciousIpThreshold) {
            logSecurityEvent("SYSTEM", "SUSPICIOUS_IP_ACTIVITY", ipAddress, null,
                    String.format("Multiple failed login attempts from IP: %s", ipAddress), false);
            
            // Apply rate limiting for suspicious IP
            rateLimitService.addToBlacklist(ipAddress);
        }
    }

    /**
     * Clean up old IP tracking data
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupIpTrackingData() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        ipLoginAttempts.forEach((ip, attempts) -> {
            attempts.removeIf(attempt -> attempt.timestamp.isBefore(threshold));
        });
        ipLoginAttempts.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        log.debug("Cleaned up IP tracking data");
    }
}

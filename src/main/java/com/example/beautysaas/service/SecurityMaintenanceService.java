package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityMaintenanceService {

    private final SecurityService securityService;

    /**
     * Clean up expired account lockouts every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredLockouts() {
        try {
            securityService.unlockExpiredAccounts();
            log.debug("Completed cleanup of expired account lockouts");
        } catch (Exception e) {
            log.error("Error during lockout cleanup: {}", e.getMessage());
        }
    }

    /**
     * Log security maintenance activity every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void logSecurityMaintenance() {
        securityService.logSecurityEvent("SYSTEM", "SECURITY_MAINTENANCE",
            null, null, "Scheduled security maintenance completed", true);
    }
}

package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityMonitoringService {
    private final SecurityService securityService;
    private final Map<String, AtomicInteger> suspiciousActivityCount = new ConcurrentHashMap<>();
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void monitorSuspiciousActivity() {
        log.debug("Running security monitoring check");
        // Clean up old entries
        cleanupOldEntries();
        
        // Check for suspicious patterns
        checkForSuspiciousPatterns();
    }
    
    private void cleanupOldEntries() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        // Cleanup logic here
    }
    
    private void checkForSuspiciousPatterns() {
        suspiciousActivityCount.forEach((ip, count) -> {
            if (count.get() > 10) {
                log.warn("Suspicious activity detected from IP: {}", ip);
                // Trigger additional security measures
                securityService.logSecurityEvent(
                    "SYSTEM",
                    "SUSPICIOUS_ACTIVITY",
                    ip,
                    null,
                    "High frequency of suspicious actions detected",
                    false
                );
            }
        });
    }
    
    public void recordSuspiciousActivity(String ipAddress) {
        suspiciousActivityCount.computeIfAbsent(ipAddress, k -> new AtomicInteger(0))
                              .incrementAndGet();
    }
}

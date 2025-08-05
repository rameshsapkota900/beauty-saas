package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class BruteForceProtectionService {
    
    private final SecurityEventNotifierService eventNotifier;
    private final Map<String, AttemptsInfo> attemptsCache = new ConcurrentHashMap<>();
    
    private static class AttemptsInfo {
        final AtomicInteger attempts;
        LocalDateTime firstAttempt;
        LocalDateTime lastAttempt;
        
        AttemptsInfo() {
            this.attempts = new AtomicInteger(1);
            this.firstAttempt = LocalDateTime.now();
            this.lastAttempt = LocalDateTime.now();
        }
        
        void recordAttempt() {
            attempts.incrementAndGet();
            lastAttempt = LocalDateTime.now();
        }
        
        boolean isRecentBurst() {
            return lastAttempt.minusSeconds(30).isBefore(firstAttempt) && 
                   attempts.get() > 10;
        }
        
        boolean isExtendedAttack() {
            return lastAttempt.minusMinutes(5).isBefore(firstAttempt) && 
                   attempts.get() > 30;
        }
    }
    
    public void recordLoginAttempt(String ipAddress, String userAgent) {
        AttemptsInfo info = attemptsCache.compute(ipAddress, (key, existing) -> {
            if (existing == null) {
                return new AttemptsInfo();
            }
            existing.recordAttempt();
            return existing;
        });
        
        // Check for attack patterns
        if (info.isRecentBurst()) {
            eventNotifier.notifySecurityEvent(
                "BRUTE_FORCE_BURST",
                "SYSTEM",
                "Rapid login attempts detected from IP: " + ipAddress
            );
        }
        
        if (info.isExtendedAttack()) {
            eventNotifier.notifySecurityEvent(
                "SUSTAINED_ATTACK",
                "SYSTEM",
                "Sustained attack detected from IP: " + ipAddress
            );
        }
    }
    
    public void clearStaleEntries() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        attemptsCache.entrySet().removeIf(entry -> 
            entry.getValue().lastAttempt.isBefore(threshold));
    }
    
    public boolean isSuspectedAttacker(String ipAddress) {
        AttemptsInfo info = attemptsCache.get(ipAddress);
        return info != null && (info.isRecentBurst() || info.isExtendedAttack());
    }
    
    public Map<String, Integer> getSuspiciousIPs() {
        Map<String, Integer> suspicious = new ConcurrentHashMap<>();
        attemptsCache.forEach((ip, info) -> {
            if (info.isRecentBurst() || info.isExtendedAttack()) {
                suspicious.put(ip, info.attempts.get());
            }
        });
        return suspicious;
    }
}

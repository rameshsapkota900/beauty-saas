package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class SecurityViolationTracker {
    private final Map<String, AtomicInteger> violationCountMap = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockoutMap = new ConcurrentHashMap<>();
    
    private static final int MAX_VIOLATIONS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    public void recordViolation(String userId) {
        // Check if user is in lockout
        if (isLockedOut(userId)) {
            throw new SecurityException("Account is temporarily locked due to security violations");
        }

        // Record violation
        AtomicInteger count = violationCountMap.computeIfAbsent(userId, k -> new AtomicInteger(0));
        int newCount = count.incrementAndGet();

        // Check if should lockout
        if (newCount >= MAX_VIOLATIONS) {
            lockoutUser(userId);
        }
    }

    public boolean isLockedOut(String userId) {
        LocalDateTime lockoutTime = lockoutMap.get(userId);
        if (lockoutTime == null) return false;

        if (LocalDateTime.now().isAfter(lockoutTime.plusMinutes(LOCKOUT_DURATION_MINUTES))) {
            // Lockout expired, remove it
            lockoutMap.remove(userId);
            violationCountMap.remove(userId);
            return false;
        }

        return true;
    }

    private void lockoutUser(String userId) {
        lockoutMap.put(userId, LocalDateTime.now());
    }

    public void clearViolations(String userId) {
        violationCountMap.remove(userId);
        lockoutMap.remove(userId);
    }
}

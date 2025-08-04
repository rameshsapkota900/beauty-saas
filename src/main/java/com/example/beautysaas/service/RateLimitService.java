package com.example.beautysaas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class RateLimitService {
    
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> windowStartTimes = new ConcurrentHashMap<>();
    
    private static final int DEFAULT_LIMIT = 100; // requests per window
    private static final long WINDOW_SIZE_MILLIS = 60000; // 1 minute
    
    public boolean isAllowed(String clientId) {
        return isAllowed(clientId, DEFAULT_LIMIT);
    }
    
    public boolean isAllowed(String clientId, int limit) {
        long currentTime = System.currentTimeMillis();
        
        // Clean up old entries
        cleanupOldEntries(currentTime);
        
        // Get or create window start time for this client
        windowStartTimes.putIfAbsent(clientId, currentTime);
        long windowStart = windowStartTimes.get(clientId);
        
        // Check if we're in a new window
        if (currentTime - windowStart >= WINDOW_SIZE_MILLIS) {
            windowStartTimes.put(clientId, currentTime);
            requestCounts.put(clientId, new AtomicInteger(0));
        }
        
        // Get current count and increment
        AtomicInteger count = requestCounts.computeIfAbsent(clientId, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();
        
        boolean allowed = currentCount <= limit;
        
        if (!allowed) {
            log.warn("Rate limit exceeded for client: {} (count: {}, limit: {})", clientId, currentCount, limit);
        } else {
            log.debug("Rate limit check passed for client: {} (count: {}, limit: {})", clientId, currentCount, limit);
        }
        
        return allowed;
    }
    
    public int getCurrentCount(String clientId) {
        AtomicInteger count = requestCounts.get(clientId);
        return count != null ? count.get() : 0;
    }
    
    public long getRemainingTime(String clientId) {
        Long windowStart = windowStartTimes.get(clientId);
        if (windowStart == null) return 0;
        
        long elapsed = System.currentTimeMillis() - windowStart;
        return Math.max(0, WINDOW_SIZE_MILLIS - elapsed);
    }
    
    private void cleanupOldEntries(long currentTime) {
        windowStartTimes.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() >= WINDOW_SIZE_MILLIS * 2);
        
        requestCounts.entrySet().removeIf(entry -> 
            !windowStartTimes.containsKey(entry.getKey()));
    }
    
    /**
     * Reset rate limit counters for a specific client (useful for admin actions or testing)
     */
    public void resetRateLimit(String clientId) {
        requestCounts.remove(clientId);
        windowStartTimes.remove(clientId);
        log.info("Rate limit reset for client: {}", clientId);
    }
}

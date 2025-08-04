package com.example.beautysaas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class RateLimitService {

    private final ConcurrentHashMap<String, UserRateLimit> rateLimitMap = new ConcurrentHashMap<>();
    
    // Default rate limits
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 60;
    private static final int LOGIN_REQUESTS_PER_MINUTE = 5;
    private static final int API_REQUESTS_PER_MINUTE = 100;

    /**
     * Check if user has exceeded rate limit for general operations
     */
    public boolean isRateLimited(String identifier) {
        return checkRateLimit(identifier, DEFAULT_REQUESTS_PER_MINUTE);
    }

    /**
     * Check if user has exceeded rate limit for login attempts
     */
    public boolean isLoginRateLimited(String identifier) {
        return checkRateLimit("login:" + identifier, LOGIN_REQUESTS_PER_MINUTE);
    }

    /**
     * Check if user has exceeded rate limit for API calls
     */
    public boolean isApiRateLimited(String identifier) {
        return checkRateLimit("api:" + identifier, API_REQUESTS_PER_MINUTE);
    }

    /**
     * Record an API request
     */
    public void recordRequest(String identifier) {
        recordRequest(identifier, DEFAULT_REQUESTS_PER_MINUTE);
    }

    /**
     * Record a login attempt
     */
    public void recordLoginAttempt(String identifier) {
        recordRequest("login:" + identifier, LOGIN_REQUESTS_PER_MINUTE);
    }

    /**
     * Record an API call
     */
    public void recordApiCall(String identifier) {
        recordRequest("api:" + identifier, API_REQUESTS_PER_MINUTE);
    }

    /**
     * Get remaining requests for identifier
     */
    public int getRemainingRequests(String identifier) {
        return getRemainingRequests(identifier, DEFAULT_REQUESTS_PER_MINUTE);
    }

    /**
     * Reset rate limit for identifier (admin function)
     */
    public void resetRateLimit(String identifier) {
        rateLimitMap.remove(identifier);
        log.info("Rate limit reset for identifier: {}", identifier);
    }

    private boolean checkRateLimit(String identifier, int maxRequests) {
        UserRateLimit rateLimit = rateLimitMap.computeIfAbsent(identifier, k -> new UserRateLimit());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(1);
        
        // Reset if window has passed
        if (rateLimit.getWindowStart().isBefore(windowStart)) {
            rateLimit.reset(now);
        }
        
        return rateLimit.getRequestCount().get() >= maxRequests;
    }

    private void recordRequest(String identifier, int maxRequests) {
        UserRateLimit rateLimit = rateLimitMap.computeIfAbsent(identifier, k -> new UserRateLimit());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(1);
        
        // Reset if window has passed
        if (rateLimit.getWindowStart().isBefore(windowStart)) {
            rateLimit.reset(now);
        }
        
        int currentCount = rateLimit.getRequestCount().incrementAndGet();
        
        if (currentCount > maxRequests) {
            log.warn("Rate limit exceeded for identifier: {} ({})", identifier, currentCount);
        }
    }

    private int getRemainingRequests(String identifier, int maxRequests) {
        UserRateLimit rateLimit = rateLimitMap.get(identifier);
        if (rateLimit == null) {
            return maxRequests;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(1);
        
        // Reset if window has passed
        if (rateLimit.getWindowStart().isBefore(windowStart)) {
            return maxRequests;
        }
        
        return Math.max(0, maxRequests - rateLimit.getRequestCount().get());
    }

    /**
     * Clean up old entries (should be called periodically)
     */
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
        rateLimitMap.entrySet().removeIf(entry -> 
            entry.getValue().getWindowStart().isBefore(cutoff));
        
        log.debug("Rate limit cleanup completed. Active entries: {}", rateLimitMap.size());
    }

    private static class UserRateLimit {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private volatile LocalDateTime windowStart = LocalDateTime.now();

        public AtomicInteger getRequestCount() {
            return requestCount;
        }

        public LocalDateTime getWindowStart() {
            return windowStart;
        }

        public void reset(LocalDateTime newWindowStart) {
            this.windowStart = newWindowStart;
            this.requestCount.set(0);
        }
    }
}

package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token_blacklist:";
    
    /**
     * Add a token to the blacklist
     */
    public void blacklistToken(String token, long timeToLiveMinutes) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", timeToLiveMinutes, TimeUnit.MINUTES);
        log.info("Token added to blacklist with TTL of {} minutes", timeToLiveMinutes);
    }
    
    /**
     * Check if a token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }
    
    /**
     * Remove a token from the blacklist
     */
    public void removeFromBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.delete(key);
        log.info("Token removed from blacklist");
    }
    
    /**
     * Clear all expired tokens
     */
    public void clearExpiredTokens() {
        // Redis automatically removes expired keys
        log.debug("Redis handles expired token cleanup automatically");
    }
}

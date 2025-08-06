package com.example.beautysaas.service;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;

@Component
public class SessionAnalyzer {
    private final Map<String, SessionData> sessionDataMap = new ConcurrentHashMap<>();
    
    private static class SessionData {
        final LocalDateTime startTime;
        final AtomicInteger actionCount;
        final Map<String, AtomicInteger> actionTypeCount;
        
        SessionData() {
            this.startTime = LocalDateTime.now();
            this.actionCount = new AtomicInteger(0);
            this.actionTypeCount = new ConcurrentHashMap<>();
        }
    }
    
    public void recordSessionActivity(String sessionId, String actionType) {
        SessionData data = sessionDataMap.computeIfAbsent(sessionId, k -> new SessionData());
        data.actionCount.incrementAndGet();
        data.actionTypeCount.computeIfAbsent(actionType, k -> new AtomicInteger()).incrementAndGet();
    }
    
    public boolean isSessionSuspicious(String sessionId) {
        SessionData data = sessionDataMap.get(sessionId);
        if (data == null) return false;
        
        // Check for rapid actions
        double actionsPerMinute = calculateActionsPerMinute(data);
        if (actionsPerMinute > 30) return true;
        
        // Check for unusual action patterns
        if (hasUnusualActionPattern(data)) return true;
        
        return false;
    }
    
    private double calculateActionsPerMinute(SessionData data) {
        long minutesSinceStart = java.time.Duration.between(data.startTime, LocalDateTime.now()).toMinutes();
        if (minutesSinceStart == 0) minutesSinceStart = 1;
        return (double) data.actionCount.get() / minutesSinceStart;
    }
    
    private boolean hasUnusualActionPattern(SessionData data) {
        // Check if any single action type comprises more than 80% of all actions
        int totalActions = data.actionCount.get();
        return data.actionTypeCount.values().stream()
            .anyMatch(count -> (double) count.get() / totalActions > 0.8);
    }
    
    public void clearSessionData(String sessionId) {
        sessionDataMap.remove(sessionId);
    }
}

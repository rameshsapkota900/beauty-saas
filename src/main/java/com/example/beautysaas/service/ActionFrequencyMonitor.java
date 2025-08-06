package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ActionFrequencyMonitor {
    private final Map<String, Map<LocalDateTime, Integer>> actionTimeMap = new ConcurrentHashMap<>();
    private static final int TIME_WINDOW_MINUTES = 5;
    private static final int FREQUENCY_THRESHOLD = 10;

    public void recordAction(String userId, String action) {
        String key = userId + ":" + action;
        LocalDateTime now = LocalDateTime.now();
        
        // Initialize or get user's action map
        Map<LocalDateTime, Integer> timeMap = actionTimeMap.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        
        // Record current action
        timeMap.compute(now, (k, v) -> v == null ? 1 : v + 1);
        
        // Clean up old entries
        cleanupOldEntries(timeMap);
    }

    public boolean isFrequencyExceeded(String userId, String action) {
        String key = userId + ":" + action;
        Map<LocalDateTime, Integer> timeMap = actionTimeMap.get(key);
        if (timeMap == null) return false;

        // Calculate total actions in time window
        int totalActions = timeMap.entrySet().stream()
            .mapToInt(Map.Entry::getValue)
            .sum();

        return totalActions >= FREQUENCY_THRESHOLD;
    }

    private void cleanupOldEntries(Map<LocalDateTime, Integer> timeMap) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(TIME_WINDOW_MINUTES);
        timeMap.entrySet().removeIf(entry -> entry.getKey().isBefore(cutoff));
    }
}

package com.example.beautysaas.service;

import com.example.beautysaas.repository.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityMetricsService {

    private final SecurityAuditLogRepository securityAuditLogRepository;
    private final Map<String, SecurityMetric> metrics = new ConcurrentHashMap<>();
    
    /**
     * Represents a security metric with its current value and threshold
     */
    private static class SecurityMetric {
        final String name;
        final double threshold;
        double currentValue;
        LocalDateTime lastUpdated;
        boolean thresholdExceeded;
        
        SecurityMetric(String name, double threshold) {
            this.name = name;
            this.threshold = threshold;
            this.currentValue = 0.0;
            this.lastUpdated = LocalDateTime.now();
            this.thresholdExceeded = false;
        }
    }
    
    /**
     * Initialize default security metrics
     */
    public void initializeMetrics() {
        metrics.put("failed_login_rate", new SecurityMetric("Failed Login Rate", 0.3)); // 30% threshold
        metrics.put("account_lockouts", new SecurityMetric("Account Lockouts", 5.0)); // 5 lockouts per hour
        metrics.put("suspicious_ip_rate", new SecurityMetric("Suspicious IP Rate", 0.1)); // 10% threshold
        metrics.put("concurrent_sessions", new SecurityMetric("Concurrent Sessions", 3.0)); // 3 sessions per user
        metrics.put("password_change_rate", new SecurityMetric("Password Change Rate", 2.0)); // 2 changes per day
        log.info("Security metrics initialized");
    }
    
    /**
     * Update metrics based on security events
     */
    public void updateMetrics(LocalDateTime startTime, LocalDateTime endTime) {
        // Calculate failed login rate
        long totalLogins = securityAuditLogRepository.countByEventTypeAndTimestampBetween(
                "LOGIN_SUCCESS", startTime, endTime) +
                securityAuditLogRepository.countByEventTypeAndTimestampBetween(
                        "LOGIN_FAILURE", startTime, endTime);
        
        if (totalLogins > 0) {
            double failureRate = (double) securityAuditLogRepository.countByEventTypeAndTimestampBetween(
                    "LOGIN_FAILURE", startTime, endTime) / totalLogins;
            updateMetric("failed_login_rate", failureRate);
        }
        
        // Calculate account lockouts
        long lockouts = securityAuditLogRepository.countByEventTypeAndTimestampBetween(
                "ACCOUNT_LOCKED", startTime, endTime);
        updateMetric("account_lockouts", lockouts);
        
        // Calculate suspicious IP rate
        long suspiciousIPs = securityAuditLogRepository.countByEventTypeAndTimestampBetween(
                "SUSPICIOUS_IP_ACTIVITY", startTime, endTime);
        long totalIPs = securityAuditLogRepository.countDistinctIpAddressesBetween(startTime, endTime);
        
        if (totalIPs > 0) {
            double suspiciousRate = (double) suspiciousIPs / totalIPs;
            updateMetric("suspicious_ip_rate", suspiciousRate);
        }
        
        log.debug("Security metrics updated for period: {} to {}", startTime, endTime);
    }
    
    /**
     * Update a specific metric
     */
    private void updateMetric(String metricName, double value) {
        SecurityMetric metric = metrics.get(metricName);
        if (metric != null) {
            metric.currentValue = value;
            metric.lastUpdated = LocalDateTime.now();
            metric.thresholdExceeded = value > metric.threshold;
            
            if (metric.thresholdExceeded) {
                log.warn("Security metric {} exceeded threshold: {} > {}", 
                        metricName, value, metric.threshold);
            }
        }
    }
    
    /**
     * Get current security metrics
     */
    public Map<String, Map<String, Object>> getCurrentMetrics() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        metrics.forEach((name, metric) -> {
            Map<String, Object> metricData = new HashMap<>();
            metricData.put("value", metric.currentValue);
            metricData.put("threshold", metric.threshold);
            metricData.put("lastUpdated", metric.lastUpdated);
            metricData.put("thresholdExceeded", metric.thresholdExceeded);
            result.put(name, metricData);
        });
        
        return result;
    }
    
    /**
     * Get security metrics that have exceeded their thresholds
     */
    public List<String> getExceededThresholds() {
        return metrics.entrySet().stream()
                .filter(entry -> entry.getValue().thresholdExceeded)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Update metrics every 5 minutes
     */
    @Scheduled(fixedRate = 300000)
    public void scheduledMetricsUpdate() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(5);
        updateMetrics(startTime, endTime);
    }
    
    /**
     * Reset exceeded thresholds (after investigation/mitigation)
     */
    public void resetThresholdAlert(String metricName) {
        SecurityMetric metric = metrics.get(metricName);
        if (metric != null) {
            metric.thresholdExceeded = false;
            log.info("Reset threshold alert for metric: {}", metricName);
        }
    }
}

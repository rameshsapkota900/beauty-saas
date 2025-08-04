package com.example.beautysaas.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Slf4j
public class AuditUtils {
    
    private static final DateTimeFormatter AUDIT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        String xForwardedForCf = request.getHeader("CF-Connecting-IP");
        if (xForwardedForCf != null && !xForwardedForCf.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedForCf)) {
            return xForwardedForCf;
        }
        
        return request.getRemoteAddr();
    }
    
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
    public static String getSessionId(HttpServletRequest request) {
        return request.getSession(false) != null ? request.getSession().getId() : null;
    }
    
    public static Map<String, String> extractSecurityHeaders(HttpServletRequest request) {
        Map<String, String> securityHeaders = new HashMap<>();
        
        String[] securityHeaderNames = {
            "User-Agent", "X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP",
            "Referer", "Origin", "X-Requested-With", "Accept-Language"
        };
        
        for (String headerName : securityHeaderNames) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                securityHeaders.put(headerName, headerValue);
            }
        }
        
        return securityHeaders;
    }
    
    public static String formatAuditTimestamp(LocalDateTime timestamp) {
        return timestamp.format(AUDIT_DATE_FORMAT);
    }
    
    public static String buildAuditMessage(String action, String userEmail, String ipAddress, String details) {
        return String.format("[%s] User: %s | IP: %s | Action: %s | Details: %s",
            formatAuditTimestamp(LocalDateTime.now()),
            userEmail != null ? userEmail : "ANONYMOUS",
            ipAddress != null ? ipAddress : "UNKNOWN",
            action,
            details != null ? details : "No additional details"
        );
    }
    
    public static void logSecurityEvent(String level, String message) {
        switch (level.toUpperCase()) {
            case "ERROR":
                log.error("SECURITY_EVENT: {}", message);
                break;
            case "WARN":
                log.warn("SECURITY_EVENT: {}", message);
                break;
            case "INFO":
                log.info("SECURITY_EVENT: {}", message);
                break;
            default:
                log.debug("SECURITY_EVENT: {}", message);
                break;
        }
    }
    
    public static boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

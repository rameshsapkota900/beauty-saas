package com.example.beautysaas.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class AuditUtils {

    private static final DateTimeFormatter AUDIT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log user action for audit purposes
     */
    public static void logUserAction(String userEmail, String action, String details) {
        log.info("AUDIT: User={}, Action={}, Details={}, Time={}", 
            userEmail, action, details, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log system action for audit purposes
     */
    public static void logSystemAction(String action, String details) {
        log.info("AUDIT: System Action={}, Details={}, Time={}", 
            action, details, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log data modification for audit purposes
     */
    public static void logDataModification(String userEmail, String entityType, String entityId, String operation, String oldValue, String newValue) {
        log.info("AUDIT: User={}, Entity={}[{}], Operation={}, Old={}, New={}, Time={}", 
            userEmail, entityType, entityId, operation, oldValue, newValue, 
            LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log security event for audit purposes
     */
    public static void logSecurityEvent(String userEmail, String eventType, String ipAddress, String details) {
        log.warn("SECURITY_AUDIT: User={}, Event={}, IP={}, Details={}, Time={}", 
            userEmail, eventType, ipAddress, details, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log business transaction for audit purposes
     */
    public static void logBusinessTransaction(String userEmail, String transactionType, String amount, String details) {
        log.info("BUSINESS_AUDIT: User={}, Transaction={}, Amount={}, Details={}, Time={}", 
            userEmail, transactionType, amount, details, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log admin action for audit purposes
     */
    public static void logAdminAction(String adminEmail, String action, String targetUser, String details) {
        log.info("ADMIN_AUDIT: Admin={}, Action={}, Target={}, Details={}, Time={}", 
            adminEmail, action, targetUser, details, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log file operation for audit purposes
     */
    public static void logFileOperation(String userEmail, String operation, String fileName, String filePath) {
        log.info("FILE_AUDIT: User={}, Operation={}, File={}, Path={}, Time={}", 
            userEmail, operation, fileName, filePath, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }

    /**
     * Log API access for audit purposes
     */
    public static void logApiAccess(String userEmail, String endpoint, String method, String ipAddress, String userAgent) {
        log.debug("API_AUDIT: User={}, Endpoint={}, Method={}, IP={}, UserAgent={}, Time={}", 
            userEmail, endpoint, method, ipAddress, userAgent, LocalDateTime.now().format(AUDIT_DATE_FORMAT));
    }
}

package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityNotificationService {
    private final JavaMailSender mailSender;
    private final Map<String, NotificationThrottler> throttlers = new ConcurrentHashMap<>();

    private static class NotificationThrottler {
        private LocalDateTime lastNotificationTime = LocalDateTime.now();
        private int notificationCount = 0;
        private static final int MAX_NOTIFICATIONS_PER_HOUR = 5;
        
        boolean canSendNotification() {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(lastNotificationTime.plusHours(1))) {
                notificationCount = 0;
            }
            
            if (notificationCount < MAX_NOTIFICATIONS_PER_HOUR) {
                notificationCount++;
                lastNotificationTime = now;
                return true;
            }
            
            return false;
        }
    }

    @Async
    public void notifySecurityIncident(String recipient, String subject, Map<String, Object> details) {
        try {
            NotificationThrottler throttler = throttlers.computeIfAbsent(recipient, k -> new NotificationThrottler());
            
            if (!throttler.canSendNotification()) {
                log.warn("Notification throttled for recipient: {}", recipient);
                return;
            }

            String content = buildEmailContent(details);
            sendEmail(recipient, subject, content);
            
            log.info("Security incident notification sent to: {}", recipient);
        } catch (Exception e) {
            log.error("Failed to send security incident notification", e);
        }
    }

    @Async
    public void notifyAnomalousActivity(String recipient, String userId, Map<String, Object> activityDetails) {
        try {
            Map<String, Object> details = new HashMap<>(activityDetails);
            details.put("userId", userId);
            details.put("detectionTime", LocalDateTime.now());
            
            String subject = "Anomalous Activity Detected - User: " + userId;
            notifySecurityIncident(recipient, subject, details);
        } catch (Exception e) {
            log.error("Failed to send anomalous activity notification", e);
        }
    }

    @Async
    public void notifySecurityViolation(String recipient, String userId, String violationType, Map<String, Object> violationDetails) {
        try {
            Map<String, Object> details = new HashMap<>(violationDetails);
            details.put("userId", userId);
            details.put("violationType", violationType);
            details.put("detectionTime", LocalDateTime.now());
            
            String subject = "Security Violation Alert - " + violationType;
            notifySecurityIncident(recipient, subject, details);
        } catch (Exception e) {
            log.error("Failed to send security violation notification", e);
        }
    }

    private String buildEmailContent(Map<String, Object> details) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        content.append("<h2>Security Incident Report</h2>");
        content.append("<table border='1' cellpadding='5'>");
        
        details.forEach((key, value) -> {
            content.append("<tr>");
            content.append("<td><strong>").append(key).append("</strong></td>");
            content.append("<td>").append(value).append("</td>");
            content.append("</tr>");
        });
        
        content.append("</table>");
        content.append("</body></html>");
        
        return content.toString();
    }

    private void sendEmail(String recipient, String subject, String content) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(content, true);
        
        mailSender.send(message);
    }
}

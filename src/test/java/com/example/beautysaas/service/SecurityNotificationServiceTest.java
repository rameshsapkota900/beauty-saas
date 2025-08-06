package com.example.beautysaas.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityNotificationServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private SecurityNotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void notifySecurityIncident_ShouldSendEmail() throws Exception {
        // Arrange
        String recipient = "security@test.com";
        String subject = "Test Security Alert";
        Map<String, Object> details = new HashMap<>();
        details.put("event", "Unauthorized Access");
        details.put("severity", "HIGH");

        // Act
        notificationService.notifySecurityIncident(recipient, subject, details);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void notifyAnomalousActivity_ShouldSendEmail() throws Exception {
        // Arrange
        String recipient = "security@test.com";
        String userId = "testUser";
        Map<String, Object> details = new HashMap<>();
        details.put("activity", "Multiple Failed Logins");

        // Act
        notificationService.notifyAnomalousActivity(recipient, userId, details);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void notifySecurityViolation_ShouldSendEmail() throws Exception {
        // Arrange
        String recipient = "security@test.com";
        String userId = "testUser";
        String violationType = "ACCESS_DENIED";
        Map<String, Object> details = new HashMap<>();
        details.put("location", "Restricted Area");

        // Act
        notificationService.notifySecurityViolation(recipient, userId, violationType, details);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }
}

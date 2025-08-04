package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${spring.application.name:BeautySaaS}")
    private String applicationName;

    /**
     * Send welcome email to new customers
     */
    public void sendWelcomeEmail(String email, String name) {
        log.info("Sending welcome email to: {} ({})", name, email);
        
        // In a real implementation, you would use JavaMailSender
        // For now, we'll just log the email content
        String subject = "Welcome to " + applicationName;
        String body = buildWelcomeEmailBody(name);
        
        logEmail(email, subject, body);
    }

    /**
     * Send password change notification
     */
    public void sendPasswordChangeNotification(String email, String name) {
        log.info("Sending password change notification to: {} ({})", name, email);
        
        String subject = "Password Changed - " + applicationName;
        String body = buildPasswordChangeEmailBody(name);
        
        logEmail(email, subject, body);
    }

    /**
     * Send account lockout notification
     */
    public void sendAccountLockoutNotification(String email, String name, int lockoutDurationMinutes) {
        log.warn("Sending account lockout notification to: {} ({})", name, email);
        
        String subject = "Account Temporarily Locked - " + applicationName;
        String body = buildAccountLockoutEmailBody(name, lockoutDurationMinutes);
        
        logEmail(email, subject, body);
    }

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(String email, String customerName, String serviceName, LocalDateTime bookingTime, String parlourName) {
        log.info("Sending booking confirmation to: {} for service: {}", email, serviceName);
        
        String subject = "Booking Confirmed - " + serviceName;
        String body = buildBookingConfirmationEmailBody(customerName, serviceName, bookingTime, parlourName);
        
        logEmail(email, subject, body);
    }

    /**
     * Send new parlour notification to super admin
     */
    public void sendNewParlourNotification(String superAdminEmail, String parlourName, String adminEmail) {
        log.info("Sending new parlour notification to super admin: {}", superAdminEmail);
        
        String subject = "New Parlour Created - " + parlourName;
        String body = buildNewParlourEmailBody(parlourName, adminEmail);
        
        logEmail(superAdminEmail, subject, body);
    }

    private String buildWelcomeEmailBody(String name) {
        return String.format("""
            Dear %s,
            
            Welcome to %s! We're excited to have you as part of our beauty community.
            
            Your account has been successfully created. You can now:
            - Browse and book beauty services
            - Enroll in beauty courses
            - Manage your profile and bookings
            
            If you have any questions, please don't hesitate to contact us.
            
            Best regards,
            The %s Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """, name, applicationName, applicationName);
    }

    private String buildPasswordChangeEmailBody(String name) {
        return String.format("""
            Dear %s,
            
            Your password has been successfully changed on %s.
            
            Time: %s
            
            If you did not make this change, please contact us immediately.
            
            Best regards,
            The %s Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """, name, applicationName, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            applicationName);
    }

    private String buildAccountLockoutEmailBody(String name, int lockoutDurationMinutes) {
        return String.format("""
            Dear %s,
            
            Your account has been temporarily locked due to multiple failed login attempts.
            
            Lockout Duration: %d minutes
            Time: %s
            
            For security reasons, please wait for the lockout period to expire before attempting to log in again.
            If you believe this is an error, please contact our support team.
            
            Best regards,
            The %s Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """, name, lockoutDurationMinutes,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            applicationName);
    }

    private String buildBookingConfirmationEmailBody(String customerName, String serviceName, LocalDateTime bookingTime, String parlourName) {
        return String.format("""
            Dear %s,
            
            Your booking has been confirmed!
            
            Service: %s
            Date & Time: %s
            Parlour: %s
            
            We look forward to serving you. Please arrive 10 minutes before your appointment time.
            
            If you need to cancel or reschedule, please contact the parlour directly.
            
            Best regards,
            The %s Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """, customerName, serviceName,
            bookingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            parlourName, applicationName);
    }

    private String buildNewParlourEmailBody(String parlourName, String adminEmail) {
        return String.format("""
            Dear Super Admin,
            
            A new parlour has been registered on the platform:
            
            Parlour Name: %s
            Admin Email: %s
            Registration Time: %s
            
            Please review the parlour details and ensure everything is in order.
            
            Best regards,
            The %s System
            
            ---
            This is an automated message. Please do not reply to this email.
            """, parlourName, adminEmail,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            applicationName);
    }

    private void logEmail(String to, String subject, String body) {
        log.info("EMAIL SENT:");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("---END EMAIL---");
    }
}

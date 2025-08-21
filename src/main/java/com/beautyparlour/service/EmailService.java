package com.beautyparlour.service;

/**
 * Email service interface for sending notifications
 */
public interface EmailService {
    
    /**
     * Send booking confirmation email
     */
    void sendBookingConfirmation(String toEmail, String clientName, String serviceName, String parlourName);
    
    /**
     * Send booking status update email
     */
    void sendBookingStatusUpdate(String toEmail, String clientName, String serviceName, String status);
    
    /**
     * Send welcome email to new parlour admin
     */
    void sendWelcomeEmail(String toEmail, String adminName, String parlourName, String loginUrl);
    
    /**
     * Send payment confirmation email
     */
    void sendPaymentConfirmation(String toEmail, String clientName, String amount, String serviceName);
    
    /**
     * Send certificate completion email
     */
    void sendCertificateCompletion(String toEmail, String studentName, String courseName, String certificateUrl);
}

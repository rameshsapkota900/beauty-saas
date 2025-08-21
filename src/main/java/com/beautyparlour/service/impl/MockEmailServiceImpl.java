package com.beautyparlour.service.impl;

import com.beautyparlour.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of EmailService for development
 * Replace with actual email service implementation in production
 */
@Service
public class MockEmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockEmailServiceImpl.class);

    @Override
    public void sendBookingConfirmation(String toEmail, String clientName, String serviceName, String parlourName) {
        logger.info("MOCK EMAIL: Booking confirmation sent to {} for {} - Service: {} at {}", 
                   toEmail, clientName, serviceName, parlourName);
    }

    @Override
    public void sendBookingStatusUpdate(String toEmail, String clientName, String serviceName, String status) {
        logger.info("MOCK EMAIL: Booking status update sent to {} for {} - Service: {} - Status: {}", 
                   toEmail, clientName, serviceName, status);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String adminName, String parlourName, String loginUrl) {
        logger.info("MOCK EMAIL: Welcome email sent to {} for admin {} of parlour {} - Login: {}", 
                   toEmail, adminName, parlourName, loginUrl);
    }

    @Override
    public void sendPaymentConfirmation(String toEmail, String clientName, String amount, String serviceName) {
        logger.info("MOCK EMAIL: Payment confirmation sent to {} for {} - Amount: {} - Service: {}", 
                   toEmail, clientName, amount, serviceName);
    }

    @Override
    public void sendCertificateCompletion(String toEmail, String studentName, String courseName, String certificateUrl) {
        logger.info("MOCK EMAIL: Certificate completion sent to {} for {} - Course: {} - Certificate: {}", 
                   toEmail, studentName, courseName, certificateUrl);
    }
}

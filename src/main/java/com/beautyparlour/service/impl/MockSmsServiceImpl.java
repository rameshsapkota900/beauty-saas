package com.beautyparlour.service.impl;

import com.beautyparlour.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of SmsService for development
 * Replace with actual SMS service implementation in production
 */
@Service
public class MockSmsServiceImpl implements SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockSmsServiceImpl.class);

    @Override
    public void sendBookingConfirmationSms(String phoneNumber, String clientName, String serviceName, String parlourName) {
        logger.info("MOCK SMS: Booking confirmation sent to {} - {} booked {} at {}", 
                   phoneNumber, clientName, serviceName, parlourName);
    }

    @Override
    public void sendBookingReminderSms(String phoneNumber, String clientName, String serviceName, String appointmentTime) {
        logger.info("MOCK SMS: Booking reminder sent to {} - {} has {} appointment at {}", 
                   phoneNumber, clientName, serviceName, appointmentTime);
    }

    @Override
    public void sendBookingStatusSms(String phoneNumber, String clientName, String serviceName, String status) {
        logger.info("MOCK SMS: Status update sent to {} - {} booking for {} is now {}", 
                   phoneNumber, clientName, serviceName, status);
    }

    @Override
    public void sendOtpSms(String phoneNumber, String otp) {
        logger.info("MOCK SMS: OTP {} sent to {}", otp, phoneNumber);
    }

    @Override
    public void sendPromotionalSms(String phoneNumber, String message, String parlourName) {
        logger.info("MOCK SMS: Promotional message from {} sent to {}: {}", 
                   parlourName, phoneNumber, message);
    }
}

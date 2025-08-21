package com.beautyparlour.service;

/**
 * SMS service interface for sending text notifications
 */
public interface SmsService {
    
    /**
     * Send booking confirmation SMS
     */
    void sendBookingConfirmationSms(String phoneNumber, String clientName, String serviceName, String parlourName);
    
    /**
     * Send booking reminder SMS
     */
    void sendBookingReminderSms(String phoneNumber, String clientName, String serviceName, String appointmentTime);
    
    /**
     * Send booking status update SMS
     */
    void sendBookingStatusSms(String phoneNumber, String clientName, String serviceName, String status);
    
    /**
     * Send OTP for verification
     */
    void sendOtpSms(String phoneNumber, String otp);
    
    /**
     * Send promotional SMS
     */
    void sendPromotionalSms(String phoneNumber, String message, String parlourName);
}

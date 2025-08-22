package com.beautyparlour.service;

import java.util.UUID;

/**
 * Service interface for various notification operations
 */
public interface NotificationService {
    
    /**
     * Send push notification to mobile app
     */
    void sendPushNotification(String deviceToken, String title, String message);
    
    /**
     * Send SMS notification
     */
    void sendSmsNotification(String phoneNumber, String message);
    
    /**
     * Send booking reminder notification
     */
    void sendBookingReminder(UUID bookingId, String clientPhone, String clientEmail);
    
    /**
     * Send payment reminder to staff
     */
    void sendPaymentReminder(UUID staffId, String staffPhone);
    
    /**
     * Send promotional notification
     */
    void sendPromotionalNotification(UUID parlourId, String message);
    
    /**
     * Send system alert to admin
     */
    void sendSystemAlert(UUID parlourId, String alertType, String message);
}

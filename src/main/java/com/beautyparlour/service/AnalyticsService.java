package com.beautyparlour.service;

import com.beautyparlour.dto.response.AnalyticsDTO;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Analytics service for business intelligence and reporting
 */
public interface AnalyticsService {
    
    /**
     * Get comprehensive parlour analytics
     */
    AnalyticsDTO getParlourAnalytics(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get booking statistics by status
     */
    Map<String, Long> getBookingStatusStats(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get revenue analytics
     */
    Map<String, Object> getRevenueAnalytics(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get popular services statistics
     */
    Map<String, Long> getPopularServicesStats(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get staff performance metrics
     */
    Map<String, Object> getStaffPerformanceMetrics(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get customer analytics
     */
    Map<String, Object> getCustomerAnalytics(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get course enrollment statistics
     */
    Map<String, Long> getCourseEnrollmentStats(UUID parlourId, LocalDateTime startDate, LocalDateTime endDate);
}

package com.beautyparlour.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for booking analytics data
 */
public class BookingAnalyticsDTO {
    private long totalBookings;
    private long pendingBookings;
    private long acceptedBookings;
    private long completedBookings;
    private long cancelledBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageBookingValue;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String mostPopularService;
    private long mostPopularServiceCount;
    private double completionRate;
    private double cancellationRate;

    public BookingAnalyticsDTO() {}

    public BookingAnalyticsDTO(long totalBookings, long pendingBookings, long acceptedBookings, 
                              long completedBookings, long cancelledBookings) {
        this.totalBookings = totalBookings;
        this.pendingBookings = pendingBookings;
        this.acceptedBookings = acceptedBookings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        
        // Calculate rates
        if (totalBookings > 0) {
            this.completionRate = (double) completedBookings / totalBookings * 100;
            this.cancellationRate = (double) cancelledBookings / totalBookings * 100;
        }
    }

    // Getters and setters
    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getAcceptedBookings() {
        return acceptedBookings;
    }

    public void setAcceptedBookings(long acceptedBookings) {
        this.acceptedBookings = acceptedBookings;
    }

    public long getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(long completedBookings) {
        this.completedBookings = completedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageBookingValue() {
        return averageBookingValue;
    }

    public void setAverageBookingValue(BigDecimal averageBookingValue) {
        this.averageBookingValue = averageBookingValue;
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getMostPopularService() {
        return mostPopularService;
    }

    public void setMostPopularService(String mostPopularService) {
        this.mostPopularService = mostPopularService;
    }

    public long getMostPopularServiceCount() {
        return mostPopularServiceCount;
    }

    public void setMostPopularServiceCount(long mostPopularServiceCount) {
        this.mostPopularServiceCount = mostPopularServiceCount;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }
}

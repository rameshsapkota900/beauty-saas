package com.beautyparlour.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for comprehensive analytics data
 */
public class AnalyticsDTO {
    private UUID parlourId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private long totalBookings;
    private long completedBookings;
    private long cancelledBookings;
    private long pendingBookings;
    private double completionRate;
    private double cancellationRate;
    private long totalCustomers;
    private long newCustomers;
    private long repeatCustomers;
    private double averageBookingsPerCustomer;
    private Map<String, Long> popularServices;
    private Map<String, Long> bookingsByStatus;
    private Map<String, Object> revenueData;
    private Map<String, Long> dailyBookings;

    public AnalyticsDTO() {}

    // Getters and setters
    public UUID getParlourId() {
        return parlourId;
    }

    public void setParlourId(UUID parlourId) {
        this.parlourId = parlourId;
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

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
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

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
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

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(long newCustomers) {
        this.newCustomers = newCustomers;
    }

    public long getRepeatCustomers() {
        return repeatCustomers;
    }

    public void setRepeatCustomers(long repeatCustomers) {
        this.repeatCustomers = repeatCustomers;
    }

    public double getAverageBookingsPerCustomer() {
        return averageBookingsPerCustomer;
    }

    public void setAverageBookingsPerCustomer(double averageBookingsPerCustomer) {
        this.averageBookingsPerCustomer = averageBookingsPerCustomer;
    }

    public Map<String, Long> getPopularServices() {
        return popularServices;
    }

    public void setPopularServices(Map<String, Long> popularServices) {
        this.popularServices = popularServices;
    }

    public Map<String, Long> getBookingsByStatus() {
        return bookingsByStatus;
    }

    public void setBookingsByStatus(Map<String, Long> bookingsByStatus) {
        this.bookingsByStatus = bookingsByStatus;
    }

    public Map<String, Object> getRevenueData() {
        return revenueData;
    }

    public void setRevenueData(Map<String, Object> revenueData) {
        this.revenueData = revenueData;
    }

    public Map<String, Long> getDailyBookings() {
        return dailyBookings;
    }

    public void setDailyBookings(Map<String, Long> dailyBookings) {
        this.dailyBookings = dailyBookings;
    }
}

package com.beautyparlour.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for dashboard analytics data
 */
public class DashboardAnalyticsDTO {
    private long totalBookings;
    private long completedBookings;
    private long pendingBookings;
    private long cancelledBookings;
    private long totalStaff;
    private long totalServices;
    private long totalCourses;
    private BigDecimal totalRevenue;
    private LocalDate reportDate;
    
    // Booking growth metrics
    private long bookingsThisMonth;
    private long bookingsLastMonth;
    private double bookingGrowthPercentage;
    
    // Popular services
    private String mostPopularService;
    private long mostPopularServiceBookings;
    
    public DashboardAnalyticsDTO() {}

    // Getters and setters
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

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public long getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(long totalStaff) {
        this.totalStaff = totalStaff;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(long totalServices) {
        this.totalServices = totalServices;
    }

    public long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public long getBookingsThisMonth() {
        return bookingsThisMonth;
    }

    public void setBookingsThisMonth(long bookingsThisMonth) {
        this.bookingsThisMonth = bookingsThisMonth;
    }

    public long getBookingsLastMonth() {
        return bookingsLastMonth;
    }

    public void setBookingsLastMonth(long bookingsLastMonth) {
        this.bookingsLastMonth = bookingsLastMonth;
    }

    public double getBookingGrowthPercentage() {
        return bookingGrowthPercentage;
    }

    public void setBookingGrowthPercentage(double bookingGrowthPercentage) {
        this.bookingGrowthPercentage = bookingGrowthPercentage;
    }

    public String getMostPopularService() {
        return mostPopularService;
    }

    public void setMostPopularService(String mostPopularService) {
        this.mostPopularService = mostPopularService;
    }

    public long getMostPopularServiceBookings() {
        return mostPopularServiceBookings;
    }

    public void setMostPopularServiceBookings(long mostPopularServiceBookings) {
        this.mostPopularServiceBookings = mostPopularServiceBookings;
    }
}

package com.example.beautysaas.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DashboardAnalyticsDto {
    // Common metrics
    private Long totalUsers;
    private Long totalParlours;
    private Long totalBookings;
    private BigDecimal totalRevenue;

    // Parlour-specific metrics (for Admin dashboard)
    private Long parlourTotalServices;
    private Long parlourTotalCourses;
    private Long parlourTotalStaff;
    private Long parlourTotalProducts;
    private Long parlourPendingBookings;
    private Long parlourConfirmedBookings;
    private Long parlourCompletedBookings;
    private BigDecimal parlourMonthlyRevenue;
    private Map<String, Long> bookingsByStatus; // e.g., PENDING: 10, CONFIRMED: 5
    private Map<String, Long> bookingsByType; // e.g., SERVICE: 15, COURSE: 3
    private Map<String, BigDecimal> revenueByCategory; // e.g., Haircut: 500.00, Massage: 300.00
    private Map<String, Long> topPerformingStaff; // Staff Name -> Number of bookings
    private Map<String, Long> topBookedServicesCourses; // Item Name -> Number of bookings

    // SuperAdmin-specific metrics (platform-wide)
    private Map<String, Long> parloursByStatus; // e.g., ACTIVE: 5, INACTIVE: 2
    private Map<String, BigDecimal> revenueByParlour; // Parlour Name -> Revenue
}

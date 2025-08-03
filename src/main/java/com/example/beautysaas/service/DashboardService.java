package com.example.beautysaas.service;

import com.example.beautysaas.dto.dashboard.DashboardAnalyticsDto;
import com.example.beautysaas.entity.Booking;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.BookingRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.ProductRepository;
import com.example.beautysaas.repository.ServiceRepository;
import com.example.beautysaas.repository.StaffRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardService {

    private final UserRepository userRepository;
    private final ParlourRepository parlourRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final CourseService courseService; // Assuming CourseService has count method
    private final StaffRepository staffRepository;
    private final ProductRepository productRepository;

    public DashboardService(UserRepository userRepository,
                            ParlourRepository parlourRepository,
                            BookingRepository bookingRepository,
                            ServiceRepository serviceRepository,
                            CourseService courseService,
                            StaffRepository staffRepository,
                            ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.parlourRepository = parlourRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.courseService = courseService;
        this.staffRepository = staffRepository;
        this.productRepository = productRepository;
    }

    public DashboardAnalyticsDto getAdminDashboardAnalytics(String adminEmail, UUID parlourId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) \{
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour's dashboard.");
        }

        DashboardAnalyticsDto dto = new DashboardAnalyticsDto();

        // Parlour-specific metrics
        dto.setParlourTotalServices(serviceRepository.countByParlourId(parlourId));
        dto.setParlourTotalCourses(courseService.countCoursesByParlourId(parlourId)); // Assuming CourseService has this
        dto.setParlourTotalStaff(staffRepository.countByParlourId(parlourId));
        dto.setParlourTotalProducts(productRepository.countByParlourId(parlourId));

        dto.setParlourPendingBookings(bookingRepository.countByParlourIdAndStatus(parlourId, Booking.BookingStatus.PENDING));
        dto.setParlourConfirmedBookings(bookingRepository.countByParlourIdAndStatus(parlourId, Booking.BookingStatus.CONFIRMED));
        dto.setParlourCompletedBookings(bookingRepository.countByParlourIdAndStatus(parlourId, Booking.BookingStatus.COMPLETED));

        // Bookings by status
        Map<String, Long> bookingsByStatus = new HashMap<>();
        Arrays.stream(Booking.BookingStatus.values()).forEach(status ->
                bookingsByStatus.put(status.name(), bookingRepository.countByParlourIdAndStatus(parlourId, status))
        );
        dto.setBookingsByStatus(bookingsByStatus);

        // Bookings by type
        Map<String, Long> bookingsByType = new HashMap<>();
        Arrays.stream(Booking.BookingType.values()).forEach(type ->
                bookingsByType.put(type.name(), bookingRepository.countByParlourIdAndBookingType(parlourId, type))
        );
        dto.setBookingsByType(bookingsByType);

        // Monthly Revenue (e.g., for current month)
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        BigDecimal monthlyRevenue = bookingRepository.sumCompletedBookingRevenueByParlourAndPeriod(parlourId, startOfMonth, endOfMonth);
        dto.setParlourMonthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);

        // Top performing staff (e.g., top 5)
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> topStaffRaw = bookingRepository.countBookingsByStaffForParlour(parlourId, top5);
        Map<String, Long> topPerformingStaff = topStaffRaw.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0], // Staff Name
                        arr -> (Long) arr[1]    // Count
                ));
        dto.setTopPerformingStaff(topPerformingStaff);

        // Top booked services/courses (e.g., top 5)
        List<Object[]> topItemsRaw = bookingRepository.countBookingsByItemForParlour(parlourId, top5);
        Map<String, Long> topBookedServicesCourses = topItemsRaw.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0], // Item Name
                        arr -> (Long) arr[1]    // Count
                ));
        dto.setTopBookedServicesCourses(topBookedServicesCourses);

        // Revenue by category (more complex, might need custom query or in-memory aggregation)
        // For simplicity, this is left as a placeholder.
        dto.setRevenueByCategory(new HashMap<>());

        log.info("Admin dashboard analytics fetched for parlour {}.", parlourId);
        return dto;
    }

    public DashboardAnalyticsDto getSuperAdminDashboardAnalytics() {
        DashboardAnalyticsDto dto = new DashboardAnalyticsDto();

        // Platform-wide metrics
        dto.setTotalUsers(userRepository.count());
        dto.setTotalParlours(parlourRepository.count());
        dto.setTotalBookings(bookingRepository.count());
        BigDecimal totalRevenue = bookingRepository.sumTotalPlatformRevenue();
        dto.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Parlours by status (assuming a status field on Parlour entity, or infer from active/inactive admins)
        // For simplicity, this is left as a placeholder.
        dto.setParloursByStatus(new HashMap<>());

        // Revenue by parlour (more complex, might need custom query or in-memory aggregation)
        // For simplicity, this is left as a placeholder.
        dto.setRevenueByParlour(new HashMap<>());

        log.info("SuperAdmin dashboard analytics fetched.");
        return dto;
    }
}

package com.example.beautysaas.repository;

import com.example.beautysaas.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Page<Booking> findByCustomerEmailAndBookingType(String customerEmail, Booking.BookingType bookingType, Pageable pageable);
    Page<Booking> findByParlourIdAndBookingType(UUID parlourId, Booking.BookingType bookingType, Pageable pageable);

    // Find conflicting bookings for a specific staff member
    @Query("SELECT b FROM Booking b WHERE b.staff.id = :staffId AND b.status IN ('PENDING', 'CONFIRMED') AND " +
            "((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookingsForStaff(@Param("staffId") UUID staffId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    // Find conflicting bookings for a specific parlour (for general service/course availability)
    @Query("SELECT b FROM Booking b WHERE b.parlour.id = :parlourId AND b.status IN ('PENDING', 'CONFIRMED') AND " +
            "((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookingsForParlour(@Param("parlourId") UUID parlourId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    // For dashboard analytics
    long countByParlourId(UUID parlourId);
    long countByParlourIdAndStatus(UUID parlourId, Booking.BookingStatus status);
    long countByParlourIdAndBookingType(UUID parlourId, Booking.BookingType type);

    @Query("SELECT SUM(b.price) FROM Booking b WHERE b.parlour.id = :parlourId AND b.status = 'COMPLETED' AND b.startTime BETWEEN :startDate AND :endDate")
    BigDecimal sumCompletedBookingRevenueByParlourAndPeriod(@Param("parlourId") UUID parlourId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b.staff.name, COUNT(b) FROM Booking b WHERE b.parlour.id = :parlourId AND b.status = 'COMPLETED' GROUP BY b.staff.name ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsByStaffForParlour(@Param("parlourId") UUID parlourId, Pageable pageable);

    @Query("SELECT CASE WHEN b.bookingType = 'SERVICE' THEN s.name ELSE c.name END, COUNT(b) " +
            "FROM Booking b LEFT JOIN Service s ON b.itemId = s.id AND b.bookingType = 'SERVICE' " +
            "LEFT JOIN Course c ON b.itemId = c.id AND b.bookingType = 'COURSE' " +
            "WHERE b.parlour.id = :parlourId AND b.status = 'COMPLETED' " +
            "GROUP BY CASE WHEN b.bookingType = 'SERVICE' THEN s.name ELSE c.name END " +
            "ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsByItemForParlour(@Param("parlourId") UUID parlourId, Pageable pageable);

    @Query("SELECT SUM(b.price) FROM Booking b WHERE b.status = 'COMPLETED'")
    BigDecimal sumTotalPlatformRevenue();

    long count(); // Total bookings platform-wide
}

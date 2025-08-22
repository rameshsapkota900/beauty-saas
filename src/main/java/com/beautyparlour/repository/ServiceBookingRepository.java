package com.beautyparlour.repository;

import com.beautyparlour.entity.ServiceBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ServiceBooking entity operations.
 * Provides comprehensive data access methods for service booking management,
 * analytics, and reporting functionality.
 */
@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {
    
    /**
     * Finds all service bookings for a specific parlour.
     * @param parlourId the ID of the parlour
     * @return list of service bookings for the parlour
     */
    List<ServiceBooking> findByParlourId(UUID parlourId);
    
    /**
     * Finds service bookings for a parlour with pagination support.
     * @param parlourId the ID of the parlour
     * @param pageable pagination information
     * @return paginated list of service bookings
     */
    Page<ServiceBooking> findByParlourId(UUID parlourId, Pageable pageable);
    
    /**
     * Finds a specific booking by ID within a parlour.
     * @param id the booking ID
     * @param parlourId the parlour ID for tenant isolation
     * @return optional service booking if found
     */
    Optional<ServiceBooking> findByIdAndParlourId(UUID id, UUID parlourId);
    
    /**
     * Finds service bookings by client name and phone number.
     * @param clientName the client's name
     * @param phone the client's phone number
     * @return list of bookings for the client
     */
    List<ServiceBooking> findByClientNameAndPhone(String clientName, String phone);
    
    /**
     * Finds service bookings by parlour ID and booking status.
     * @param parlourId the parlour ID
     * @param status the booking status to filter by
     * @return list of bookings with the specified status
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.status = :status")
    List<ServiceBooking> findByParlourIdAndStatus(@Param("parlourId") UUID parlourId, 
                                                  @Param("status") ServiceBooking.BookingStatus status);
    
    /**
     * Finds service bookings within a date range for a specific parlour.
     * @param parlourId the parlour ID
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of bookings within the date range
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.createdAt BETWEEN :startDate AND :endDate")
    List<ServiceBooking> findByParlourIdAndDateRange(@Param("parlourId") UUID parlourId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Counts completed bookings for a specific parlour.
     * @param parlourId the parlour ID
     * @return number of completed bookings
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.status = 'COMPLETED'")
    long countCompletedBookingsByParlour(@Param("parlourId") UUID parlourId);
    
    /**
     * Counts bookings by status for analytics purposes.
     * @param parlourId the parlour ID
     * @param status the booking status to count
     * @return number of bookings with the specified status
     */
    long countByParlourIdAndStatus(UUID parlourId, ServiceBooking.BookingStatus status);
    
    /**
     * Gets bookings for a client within a specific date range.
     * @param clientName the client's name
     * @param phone the client's phone number
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of client bookings within the date range
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.clientName = :clientName AND sb.phone = :phone AND sb.createdAt BETWEEN :startDate AND :endDate")
    List<ServiceBooking> findByClientNameAndPhoneAndDateRange(@Param("clientName") String clientName,
                                                              @Param("phone") String phone,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);
    
    /**
     * Gets the latest bookings for a client, ordered by creation date.
     * @param clientName the client's name
     * @param phone the client's phone number
     * @param pageable pagination information to limit results
     * @return list of latest bookings for the client
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.clientName = :clientName AND sb.phone = :phone ORDER BY sb.createdAt DESC")
    List<ServiceBooking> findLatestByClientNameAndPhone(@Param("clientName") String clientName, 
                                                        @Param("phone") String phone, 
                                                        Pageable pageable);
    
    /**
     * Counts bookings created since a specific date for revenue analytics.
     * @param parlourId the parlour ID
     * @param date the date to count from
     * @return number of bookings created since the specified date
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.createdAt >= :date")
    long countBookingsSince(@Param("parlourId") UUID parlourId, @Param("date") LocalDateTime date);
}

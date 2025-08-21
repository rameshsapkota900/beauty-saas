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

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {
    List<ServiceBooking> findByParlourId(UUID parlourId);
    
    Page<ServiceBooking> findByParlourId(UUID parlourId, Pageable pageable);
    
    Optional<ServiceBooking> findByIdAndParlourId(UUID id, UUID parlourId);
    
    List<ServiceBooking> findByClientNameAndPhone(String clientName, String phone);
    
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.status = :status")
    List<ServiceBooking> findByParlourIdAndStatus(@Param("parlourId") UUID parlourId, 
                                                  @Param("status") ServiceBooking.BookingStatus status);
    
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.createdAt BETWEEN :startDate AND :endDate")
    List<ServiceBooking> findByParlourIdAndDateRange(@Param("parlourId") UUID parlourId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.parlourId = :parlourId AND sb.status = 'COMPLETED'")
    long countCompletedBookingsByParlour(@Param("parlourId") UUID parlourId);
}

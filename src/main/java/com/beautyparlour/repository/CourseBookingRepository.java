package com.beautyparlour.repository;

import com.beautyparlour.entity.CourseBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseBookingRepository extends JpaRepository<CourseBooking, UUID> {
    List<CourseBooking> findByParlourId(UUID parlourId);
    Optional<CourseBooking> findByIdAndParlourId(UUID id, UUID parlourId);

    // Search for bookings by client name and phone with proper validation
    @Query("SELECT cb FROM CourseBooking cb WHERE LOWER(cb.clientName) = LOWER(:clientName) AND cb.phone = :phone")
    List<CourseBooking> findByClientNameAndPhone(@Param("clientName") String clientName, @Param("phone") String phone);
}

package com.beautyparlour.repository;

import com.beautyparlour.entity.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {
    List<ServiceBooking> findByParlourId(UUID parlourId);
    Optional<ServiceBooking> findByIdAndParlourId(UUID id, UUID parlourId);
    List<ServiceBooking> findByClientNameAndPhone(String clientName, String phone);
}

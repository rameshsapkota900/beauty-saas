package com.example.beautysaas.repository;

import com.example.beautysaas.entity.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    
    Optional<GeoLocation> findByIpAddress(String ipAddress);
    
    @Query("SELECT g FROM GeoLocation g WHERE g.lastAccessEmail = :email " +
           "ORDER BY g.lastUpdated DESC LIMIT 1")
    Optional<GeoLocation> findLastLocationByEmail(@Param("email") String email);
}

package com.example.beautysaas.repository;

import com.example.beautysaas.entity.DeviceFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DeviceFingerprintRepository extends JpaRepository<DeviceFingerprint, Long> {
    
    Optional<DeviceFingerprint> findByEmailAndFingerprint(String email, String fingerprint);
    
    Set<DeviceFingerprint> findByFingerprint(String fingerprint);
    
    Set<DeviceFingerprint> findByEmailAndTrustScoreGreaterThanEqual(String email, Double trustScore);
}

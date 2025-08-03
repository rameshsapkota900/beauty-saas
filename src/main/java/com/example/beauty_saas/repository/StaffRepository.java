package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    Page<Staff> findByParlourId(UUID parlourId, Pageable pageable);
    Optional<Staff> findByParlourIdAndEmail(UUID parlourId, String email);
    boolean existsByParlourIdAndEmail(UUID parlourId, String email);
    long countByParlourId(UUID parlourId); // For dashboard analytics
}

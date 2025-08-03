package com.example.beautysaas.repository;

import com.example.beautysaas.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Page<Service> findByParlourId(UUID parlourId, Pageable pageable);
    Optional<Service> findByParlourIdAndName(UUID parlourId, String name);
    boolean existsByParlourIdAndNameIgnoreCase(UUID parlourId, String name);
    long countByParlourId(UUID parlourId); // For dashboard analytics
}

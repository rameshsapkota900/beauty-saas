package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.Parlour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParlourRepository extends JpaRepository<Parlour, UUID> {
    Optional<Parlour> findBySlug(String slug);
    boolean existsBySlug(String slug);
    long count(); // For dashboard analytics
}

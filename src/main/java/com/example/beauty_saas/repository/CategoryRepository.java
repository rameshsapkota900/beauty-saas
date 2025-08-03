package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findByParlourId(UUID parlourId, Pageable pageable);
    Optional<Category> findByParlourIdAndName(UUID parlourId, String name);
    boolean existsByParlourIdAndNameIgnoreCase(UUID parlourId, String name);
    long countByParlourId(UUID parlourId); // For dashboard analytics
}

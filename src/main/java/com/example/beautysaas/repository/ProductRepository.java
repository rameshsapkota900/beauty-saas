package com.example.beautysaas.repository;

import com.example.beautysaas.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByParlourId(UUID parlourId, Pageable pageable);
    Optional<Product> findByParlourIdAndName(UUID parlourId, String name);
    boolean existsByParlourIdAndNameIgnoreCase(UUID parlourId, String name);
    long countByParlourId(UUID parlourId); // For dashboard analytics
}

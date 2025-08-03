package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Page<Course> findByParlourId(UUID parlourId, Pageable pageable);
    Optional<Course> findByParlourIdAndName(UUID parlourId, String name);
    boolean existsByParlourIdAndNameIgnoreCase(UUID parlourId, String name);
    long countByParlourId(UUID parlourId); // For dashboard analytics
}

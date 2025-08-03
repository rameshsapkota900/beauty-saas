package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.SuccessfulStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuccessfulStudentRepository extends JpaRepository<SuccessfulStudent, UUID> {
    Page<SuccessfulStudent> findByParlourId(UUID parlourId, Pageable pageable);
    Optional<SuccessfulStudent> findByParlourIdAndId(UUID parlourId, UUID id);
    boolean existsByParlourIdAndStudentNameAndCourseId(UUID parlourId, String studentName, UUID courseId);
    long countByParlourId(UUID parlourId); // For dashboard analytics
}

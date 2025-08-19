package com.beautyparlour.repository;

import com.beautyparlour.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByParlourId(UUID parlourId);
    Optional<Course> findByIdAndParlourId(UUID id, UUID parlourId);
}

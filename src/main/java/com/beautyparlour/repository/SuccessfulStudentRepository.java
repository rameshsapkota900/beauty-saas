package com.beautyparlour.repository;

import com.beautyparlour.entity.SuccessfulStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuccessfulStudentRepository extends JpaRepository<SuccessfulStudent, UUID> {
    List<SuccessfulStudent> findByParlourId(UUID parlourId);
    Optional<SuccessfulStudent> findByIdAndParlourId(UUID id, UUID parlourId);
}

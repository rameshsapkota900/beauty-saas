package com.example.beautysaas.repository;

import com.example.beautysaas.entity.SuccessfulStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SuccessfulStudentRepository extends JpaRepository<SuccessfulStudent, UUID> {
    Page<SuccessfulStudent> findByParlourId(UUID parlourId, Pageable pageable);
}

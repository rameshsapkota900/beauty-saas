package com.example.beautysaas.repository;

import com.example.beautysaas.entity.SalaryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryLogRepository extends JpaRepository<SalaryLog, UUID> {
    Optional<SalaryLog> findByStaffIdAndPeriodMonth(UUID staffId, LocalDate periodMonth);
    Page<SalaryLog> findByStaffId(UUID staffId, Pageable pageable);
}

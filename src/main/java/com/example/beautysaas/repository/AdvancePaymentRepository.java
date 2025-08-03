package com.example.beautysaas.repository;

import com.example.beautysaas.entity.AdvancePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdvancePaymentRepository extends JpaRepository<AdvancePayment, UUID> {
    List<AdvancePayment> findByStaffIdAndPaymentDateBetween(UUID staffId, LocalDate startDate, LocalDate endDate);
    Page<AdvancePayment> findByStaffId(UUID staffId, Pageable pageable);
}

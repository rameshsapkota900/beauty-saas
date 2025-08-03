package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.AdvancePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface AdvancePaymentRepository extends JpaRepository<AdvancePayment, UUID> {
    Page<AdvancePayment> findByStaffId(UUID staffId, Pageable pageable);

    @Query("SELECT SUM(ap.amount) FROM AdvancePayment ap WHERE ap.staff.parlour.id = :parlourId AND ap.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByParlourIdAndDateRange(@Param("parlourId") UUID parlourId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "salary_logs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"staff_id", "period_month"})
})
@EntityListeners(AuditingEntityListener.class)
public class SalaryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "base_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "total_advance_payments", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAdvancePayments;

    @Column(name = "net_salary_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalaryPaid;

    @Column(name = "period_month", nullable = false)
    private LocalDate periodMonth; // Stores the first day of the month (e.g., 2023-10-01)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

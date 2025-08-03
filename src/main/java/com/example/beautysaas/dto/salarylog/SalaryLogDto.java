package com.example.beautysaas.dto.salarylog;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SalaryLogDto {
    private UUID id;
    private UUID staffId;
    private String staffName;
    private BigDecimal baseSalary;
    private BigDecimal totalAdvancePayments;
    private BigDecimal netSalaryPaid;
    private LocalDate periodMonth; // Represents the month for which salary is paid (e.g., 2023-10-01)
    private LocalDateTime createdAt;
}

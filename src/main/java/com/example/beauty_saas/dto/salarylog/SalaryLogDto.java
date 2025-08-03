package com.example.beauty_saas.dto.salarylog;

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
    private LocalDate paymentDate;
    private BigDecimal grossSalary;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private String notes;
    private LocalDateTime createdAt;
}

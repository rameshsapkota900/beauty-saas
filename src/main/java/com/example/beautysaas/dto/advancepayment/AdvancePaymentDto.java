package com.example.beautysaas.dto.advancepayment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdvancePaymentDto {
    private UUID id;
    private UUID staffId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String notes;
    private LocalDateTime createdAt;
}

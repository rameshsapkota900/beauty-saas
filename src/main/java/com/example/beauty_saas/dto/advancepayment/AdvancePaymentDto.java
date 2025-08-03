package com.example.beauty_saas.dto.advancepayment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class AdvancePaymentDto {
    private UUID id;
    private UUID staffId;
    private String staffName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String notes;
}

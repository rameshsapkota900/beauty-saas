package com.example.beauty_saas.dto.staff;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StaffDto {
    private UUID id;
    private UUID parlourId;
    private String parlourName;
    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private BigDecimal hourlyRate;
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

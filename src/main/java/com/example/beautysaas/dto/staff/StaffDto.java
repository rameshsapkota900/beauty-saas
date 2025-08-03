package com.example.beautysaas.dto.staff;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class StaffDto {
    private UUID id;
    private UUID parlourId;
    private String name;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String bio;
    private BigDecimal baseSalary;
    private Boolean isActive;
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

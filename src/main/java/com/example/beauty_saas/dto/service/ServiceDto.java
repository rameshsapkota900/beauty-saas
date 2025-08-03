package com.example.beauty_saas.dto.service;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ServiceDto {
    private UUID id;
    private UUID parlourId;
    private String parlourName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private UUID categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

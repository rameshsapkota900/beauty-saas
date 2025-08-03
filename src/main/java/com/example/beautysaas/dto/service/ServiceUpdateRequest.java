package com.example.beautysaas.dto.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ServiceUpdateRequest {
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private UUID categoryId;
    private Boolean isActive;
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
}

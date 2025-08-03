package com.example.beautysaas.dto.course;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CourseDto {
    private UUID id;
    private UUID parlourId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private UUID categoryId;
    private String categoryName;
    private Boolean isActive;
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

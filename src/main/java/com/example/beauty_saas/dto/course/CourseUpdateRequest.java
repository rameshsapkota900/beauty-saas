package com.example.beauty_saas.dto.course;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CourseUpdateRequest {
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private UUID categoryId;
}

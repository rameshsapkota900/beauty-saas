package com.example.beauty_saas.dto.course;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CourseCreateRequest {
    @NotBlank(message = "Course name cannot be blank")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @NotNull(message = "Duration in minutes cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;
}

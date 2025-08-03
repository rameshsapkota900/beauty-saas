package com.example.beautysaas.dto.course;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CourseCreateRequest {
    @NotBlank(message = "Course name cannot be empty")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Duration in minutes cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;

    @NotNull(message = "Is active cannot be null")
    private Boolean isActive;

    @NotNull(message = "Start time cannot be null")
    private LocalTime availableStartTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime availableEndTime;
}

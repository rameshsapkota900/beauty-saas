package com.example.beauty_saas.dto.staff;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class StaffCreateRequest {
    @NotBlank(message = "Staff name cannot be blank")
    @Size(min = 2, max = 100, message = "Staff name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Size(max = 500, message = "Specialization cannot exceed 500 characters")
    private String specialization;

    @NotNull(message = "Hourly rate cannot be null")
    @DecimalMin(value = "0.00", message = "Hourly rate must be non-negative")
    private BigDecimal hourlyRate;

    @NotNull(message = "Available start time cannot be null")
    private LocalTime availableStartTime;

    @NotNull(message = "Available end time cannot be null")
    private LocalTime availableEndTime;
}

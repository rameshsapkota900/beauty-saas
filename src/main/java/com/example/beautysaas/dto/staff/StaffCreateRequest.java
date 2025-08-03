package com.example.beautysaas.dto.staff;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class StaffCreateRequest {
    @NotBlank(message = "Staff name cannot be empty")
    @Size(min = 2, max = 100, message = "Staff name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    private String specialization;
    private String bio;

    @NotNull(message = "Base salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary cannot be negative")
    private BigDecimal baseSalary;

    @NotNull(message = "Is active cannot be null")
    private Boolean isActive;

    @NotNull(message = "Start time cannot be null")
    private LocalTime availableStartTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime availableEndTime;
}

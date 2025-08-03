package com.example.beautysaas.dto.staff;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class StaffUpdateRequest {
    @Size(min = 2, max = 100, message = "Staff name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;
    private String specialization;
    private String bio;

    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary cannot be negative")
    private BigDecimal baseSalary;

    private Boolean isActive;
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
}

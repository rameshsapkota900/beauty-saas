package com.example.beauty_saas.dto.staff;

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
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Size(max = 500, message = "Specialization cannot exceed 500 characters")
    private String specialization;

    @DecimalMin(value = "0.00", message = "Hourly rate must be non-negative")
    private BigDecimal hourlyRate;

    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
}

package com.example.beauty_saas.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingCreateRequest {
    @NotNull(message = "Item ID (service or course) cannot be null")
    private UUID itemId;

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Booking start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalDateTime endTime;

    private UUID staffId; // Optional, if a specific staff member is requested

    private String notes;
}

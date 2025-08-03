package com.example.beautysaas.dto.booking;

import com.example.beautysaas.entity.Booking;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingCreateRequest {
    @NotNull(message = "Item ID (service or course) cannot be null")
    private UUID itemId; // This can be serviceId or courseId

    @NotNull(message = "Booking type cannot be null")
    private Booking.BookingType bookingType;

    @NotNull(message = "Start time cannot be null")
    @FutureOrPresent(message = "Booking start time must be in the present or future")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @FutureOrPresent(message = "Booking end time must be in the present or future")
    private LocalDateTime endTime;

    private UUID staffId; // Optional: if a specific staff member is requested
    private String notes;
}

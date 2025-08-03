package com.example.beauty_saas.dto.booking;

import com.example.beautysaas.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingDto {
    private UUID id;
    private UUID parlourId;
    private String parlourName;
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private UUID itemId;
    private String itemName; // Name of the service or course
    private Booking.BookingType bookingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID staffId;
    private String staffName;
    private BigDecimal price;
    private Booking.BookingStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

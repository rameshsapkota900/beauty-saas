package com.example.beautysaas.dto.booking;

import com.example.beautysaas.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingDto {
    private UUID id;
    private UUID parlourId;
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private UUID itemId; // Service or Course ID
    private String itemName; // Service or Course Name
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

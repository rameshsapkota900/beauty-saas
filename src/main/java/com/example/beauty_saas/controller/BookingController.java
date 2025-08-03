package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.booking.BookingCreateRequest;
import com.example.beautysaas.dto.booking.BookingDto;
import com.example.beautysaas.entity.Booking;
import com.example.beautysaas.service.BookingService;
import com.example.beautysaas.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Bookings", description = "APIs for managing service and course bookings")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create a new service booking", description = "Allows a customer to book a service at a specific parlour.")
    @PostMapping("/customer/{parlourId}/bookings/service")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingDto> createServiceBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour where the service is booked", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody BookingCreateRequest createRequest) {
        log.info("Customer {} attempting to create service booking for parlour {}.", userDetails.getUsername(), parlourId);
        BookingDto booking = bookingService.createServiceBooking(userDetails.getUsername(), parlourId, createRequest);
        log.info("Service booking created successfully with ID: {}", booking.getId());
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new course booking", description = "Allows a customer to book a course at a specific parlour.")
    @PostMapping("/customer/{parlourId}/bookings/course")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingDto> createCourseBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour where the course is booked", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody BookingCreateRequest createRequest) {
        log.info("Customer {} attempting to create course booking for parlour {}.", userDetails.getUsername(), parlourId);
        BookingDto booking = bookingService.createCourseBooking(userDetails.getUsername(), parlourId, createRequest);
        log.info("Course booking created successfully with ID: {}", booking.getId());
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @Operation(summary = "Get customer's service bookings", description = "Retrieves a paginated list of service bookings for the authenticated customer.")
    @GetMapping("/customer/bookings/service")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingDto>> getCustomerServiceBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'createdAt,desc')", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        log.info("Customer {} fetching service bookings.", userDetails.getUsername());
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<BookingDto> bookings = bookingService.getCustomerBookings(userDetails.getUsername(), Booking.BookingType.SERVICE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get customer's course bookings", description = "Retrieves a paginated list of course bookings for the authenticated customer.")
    @GetMapping("/customer/bookings/course")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingDto>> getCustomerCourseBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'createdAt,desc')", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        log.info("Customer {} fetching course bookings.", userDetails.getUsername());
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<BookingDto> bookings = bookingService.getCustomerBookings(userDetails.getUsername(), Booking.BookingType.COURSE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get all service bookings for Admin", description = "Retrieves a paginated list of all service bookings for the admin's parlour.")
    @GetMapping("/admin/bookings/service")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingDto>> getAllServiceBookingsForAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'createdAt,desc')", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        log.info("Admin {} fetching all service bookings.", userDetails.getUsername());
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<BookingDto> bookings = bookingService.getAllBookingsForAdmin(userDetails.getUsername(), Booking.BookingType.SERVICE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get all course bookings for Admin", description = "Retrieves a paginated list of all course bookings for the admin's parlour.")
    @GetMapping("/admin/bookings/course")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingDto>> getAllCourseBookingsForAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'createdAt,desc')", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        log.info("Admin {} fetching all course bookings.", userDetails.getUsername());
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<BookingDto> bookings = bookingService.getAllBookingsForAdmin(userDetails.getUsername(), Booking.BookingType.COURSE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Update booking status", description = "Allows an admin to update the status of a booking.")
    @PutMapping("/admin/bookings/{bookingId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the booking to update", required = true) @PathVariable UUID bookingId,
            @Parameter(description = "New status for the booking (PENDING, CONFIRMED, REJECTED, COMPLETED, CANCELED)", required = true) @RequestParam String status) {
        log.info("Admin {} attempting to update status for booking {}.", userDetails.getUsername(), bookingId);
        BookingDto updatedBooking = bookingService.updateBookingStatus(userDetails.getUsername(), bookingId, status);
        log.info("Booking {} status updated to {}.", bookingId, updatedBooking.getStatus());
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(summary = "Cancel a booking", description = "Allows a customer or admin to cancel a booking.")
    @DeleteMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<String> cancelBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the booking to cancel", required = true) @PathVariable UUID bookingId) {
        log.info("User {} attempting to cancel booking {}.", userDetails.getUsername(), bookingId);
        bookingService.cancelBooking(userDetails.getUsername(), bookingId);
        log.info("Booking {} cancelled successfully.", bookingId);
        return ResponseEntity.ok("Booking cancelled successfully.");
    }
}

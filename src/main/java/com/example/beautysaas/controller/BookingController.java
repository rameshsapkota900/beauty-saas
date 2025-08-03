package com.example.beautysaas.controller;

import com.example.beautysaas.dto.booking.BookingCreateRequest;
import com.example.beautysaas.dto.booking.BookingDto;
import com.example.beautysaas.entity.Booking;
import com.example.beautysaas.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Booking Management", description = "APIs for managing service and course bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Customer Books Service", description = "Allows a customer to book a service for a specific parlour.")
    @PostMapping("/bookings/services")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingDto> customerBooksService(
            Principal principal,
            @Parameter(description = "ID of the parlour for the booking", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody BookingCreateRequest bookingCreateRequest) {
        log.info("Customer {} booking service for parlour {}: {}", principal.getName(), parlourId, bookingCreateRequest.getItemId());
        BookingDto booking = bookingService.createServiceBooking(principal.getName(), parlourId, bookingCreateRequest);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @Operation(summary = "Customer Books Course", description = "Allows a customer to book a course for a specific parlour.")
    @PostMapping("/bookings/courses")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingDto> customerBooksCourse(
            Principal principal,
            @Parameter(description = "ID of the parlour for the booking", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody BookingCreateRequest bookingCreateRequest) {
        log.info("Customer {} booking course for parlour {}: {}", principal.getName(), parlourId, bookingCreateRequest.getItemId());
        BookingDto booking = bookingService.createCourseBooking(principal.getName(), parlourId, bookingCreateRequest);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @Operation(summary = "Customer's Service Bookings", description = "Retrieves a list of service bookings made by the authenticated customer.")
    @GetMapping("/bookings/services/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingDto>> getMyServiceBookings(Principal principal, Pageable pageable) {
        log.info("Fetching customer's service bookings for user: {}", principal.getName());
        Page<BookingDto> bookings = bookingService.getCustomerBookings(principal.getName(), Booking.BookingType.SERVICE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Customer's Course Bookings", description = "Retrieves a list of course bookings made by the authenticated customer.")
    @GetMapping("/bookings/courses/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<BookingDto>> getMyCourseBookings(Principal principal, Pageable pageable) {
        log.info("Fetching customer's course bookings for user: {}", principal.getName());
        Page<BookingDto> bookings = bookingService.getCustomerBookings(principal.getName(), Booking.BookingType.COURSE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Admin View All Service Bookings", description = "Allows an Admin to view all service bookings for their parlour.")
    @GetMapping("/admin/bookings/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingDto>> adminViewAllServiceBookings(Principal principal, Pageable pageable) {
        log.info("Admin {} fetching all service bookings for their parlour.", principal.getName());
        Page<BookingDto> bookings = bookingService.getAllBookingsForAdmin(principal.getName(), Booking.BookingType.SERVICE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Admin View All Course Bookings", description = "Allows an Admin to view all course bookings for their parlour.")
    @GetMapping("/admin/bookings/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingDto>> adminViewAllCourseBookings(Principal principal, Pageable pageable) {
        log.info("Admin {} fetching all course bookings for their parlour.", principal.getName());
        Page<BookingDto> bookings = bookingService.getAllBookingsForAdmin(principal.getName(), Booking.BookingType.COURSE, pageable);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Admin Update Service Booking Status", description = "Allows an Admin to update the status of a service booking.")
    @PatchMapping("/admin/bookings/services/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> adminUpdateServiceBookingStatus(
            Principal principal,
            @PathVariable UUID id,
            @Parameter(description = "New status (e.g., CONFIRMED, REJECTED, COMPLETED, CANCELED)", required = true) @RequestParam String status) {
        log.info("Admin {} updating status of service booking {}.", principal.getName(), id);
        BookingDto updatedBooking = bookingService.updateBookingStatus(principal.getName(), id, status);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(summary = "Admin Update Course Booking Status", description = "Allows an Admin to update the status of a course booking.")
    @PatchMapping("/admin/bookings/courses/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> adminUpdateCourseBookingStatus(
            Principal principal,
            @PathVariable UUID id,
            @Parameter(description = "New status (e.g., CONFIRMED, REJECTED, COMPLETED, CANCELED)", required = true) @RequestParam String status) {
        log.info("Admin {} updating status of course booking {}.", principal.getName(), id);
        BookingDto updatedBooking = bookingService.updateBookingStatus(principal.getName(), id, status);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(summary = "Customer Cancels Service Booking", description = "Allows a customer to cancel their own service booking.")
    @DeleteMapping("/bookings/services/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> customerCancelsServiceBooking(Principal principal, @PathVariable UUID id) {
        log.info("Customer {} canceling service booking {}.", principal.getName(), id);
        bookingService.cancelBooking(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Customer Cancels Course Booking", description = "Allows a customer to cancel their own course booking.")
    @DeleteMapping("/bookings/courses/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> customerCancelsCourseBooking(Principal principal, @PathVariable UUID id) {
        log.info("Customer {} canceling course booking {}.", principal.getName(), id);
        bookingService.cancelBooking(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

package com.beautyparlour.controller;

import com.beautyparlour.dto.request.BookCourseRequest;
import com.beautyparlour.dto.request.BookServiceRequest;
import com.beautyparlour.dto.request.UpdateBookingStatusRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.dto.response.CourseBookingDTO;
import com.beautyparlour.entity.CourseBooking;
import com.beautyparlour.entity.ServiceBooking;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Bookings", description = "Booking management APIs")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Course Booking Endpoints
    @PostMapping("/book-course")
    @Operation(summary = "Book a course")
    public ResponseEntity<ApiResponse<CourseBookingDTO>> bookCourse(@Valid @RequestBody BookCourseRequest request) {
        CourseBooking booking = bookingService.bookCourse(request);
        CourseBookingDTO bookingDTO = new CourseBookingDTO(booking);
        return ResponseEntity.ok(ApiResponse.success("Course booked successfully", bookingDTO));
    }

    @GetMapping("/my-course-bookings")
    @Operation(summary = "Get client course bookings")
    public ResponseEntity<ApiResponse<List<CourseBookingDTO>>> getMyCourseBookings(
            @RequestParam String clientName,
            @RequestParam String phone) {
        List<CourseBooking> bookings = bookingService.getCourseBookingsByClient(clientName, phone);
        List<CourseBookingDTO> bookingDTOs = bookings.stream()
                .map(CourseBookingDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Course bookings retrieved successfully", bookingDTOs));
    }

    @DeleteMapping("/cancel-course/{bookingId}")
    @Operation(summary = "Cancel course booking")
    public ResponseEntity<ApiResponse<Void>> cancelCourseBooking(@PathVariable UUID bookingId) {
        bookingService.cancelCourseBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Course booking cancelled successfully"));
    }

    @GetMapping("/admin/course-bookings")
    @Operation(summary = "Get all course bookings for admin")
    public ResponseEntity<ApiResponse<List<CourseBookingDTO>>> getAdminCourseBookings(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<CourseBooking> bookings = bookingService.getCourseBookingsByParlour(currentUser.getParlourId());
        List<CourseBookingDTO> bookingDTOs = bookings.stream()
                .map(CourseBookingDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Course bookings retrieved successfully", bookingDTOs));
    }

    @PutMapping("/admin/course-bookings/{bookingId}/status")
    @Operation(summary = "Update course booking status")
    public ResponseEntity<ApiResponse<CourseBookingDTO>> updateCourseBookingStatus(
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        CourseBooking booking = bookingService.updateCourseBookingStatus(bookingId, request, currentUser.getParlourId());
        CourseBookingDTO bookingDTO = new CourseBookingDTO(booking);
        return ResponseEntity.ok(ApiResponse.success("Course booking status updated successfully", bookingDTO));
    }

    // Service Booking Endpoints
    @PostMapping("/book-service")
    @Operation(summary = "Book a service")
    public ResponseEntity<ApiResponse<ServiceBooking>> bookService(@Valid @RequestBody BookServiceRequest request) {
        ServiceBooking booking = bookingService.bookService(request);
        return ResponseEntity.ok(ApiResponse.success("Service booked successfully", booking));
    }

    @GetMapping("/my-service-bookings")
    @Operation(summary = "Get client service bookings")
    public ResponseEntity<ApiResponse<List<ServiceBooking>>> getMyServiceBookings(
            @RequestParam String clientName,
            @RequestParam String phone) {
        List<ServiceBooking> bookings = bookingService.getServiceBookingsByClient(clientName, phone);
        return ResponseEntity.ok(ApiResponse.success("Service bookings retrieved successfully", bookings));
    }

    @DeleteMapping("/cancel-service/{bookingId}")
    @Operation(summary = "Cancel service booking")
    public ResponseEntity<ApiResponse<Void>> cancelServiceBooking(@PathVariable UUID bookingId) {
        bookingService.cancelServiceBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Service booking cancelled successfully"));
    }

    @GetMapping("/admin/service-bookings")
    @Operation(summary = "Get all service bookings for admin")
    public ResponseEntity<ApiResponse<List<ServiceBooking>>> getAdminServiceBookings(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ServiceBooking> bookings = bookingService.getServiceBookingsByParlour(currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Service bookings retrieved successfully", bookings));
    }

    @PutMapping("/admin/service-bookings/{bookingId}/status")
    @Operation(summary = "Update service booking status")
    public ResponseEntity<ApiResponse<ServiceBooking>> updateServiceBookingStatus(
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ServiceBooking booking = bookingService.updateServiceBookingStatus(bookingId, request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Service booking status updated successfully", booking));
    }
}

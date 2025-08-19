package com.beautyparlour.service;

import com.beautyparlour.dto.request.BookCourseRequest;
import com.beautyparlour.dto.request.BookServiceRequest;
import com.beautyparlour.dto.request.UpdateBookingStatusRequest;
import com.beautyparlour.entity.CourseBooking;
import com.beautyparlour.entity.ServiceBooking;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.CourseBookingRepository;
import com.beautyparlour.repository.CourseRepository;
import com.beautyparlour.repository.ServiceBookingRepository;
import com.beautyparlour.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private CourseBookingRepository courseBookingRepository;

    @Autowired
    private ServiceBookingRepository serviceBookingRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    // Course Booking Methods
    public CourseBooking bookCourse(BookCourseRequest request) {
        // Verify course exists
        com.beautyparlour.entity.Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Validate phone number
        String phone = validateAndNormalizePhone(request.getPhone());

        CourseBooking booking = new CourseBooking(
                course.getParlourId(),
                request.getCourseId(),
                request.getClientName().trim(),
                phone
        );
        return courseBookingRepository.save(booking);
    }

    public List<CourseBooking> getCourseBookingsByClient(String clientName, String phone) {
        System.out.println("Searching for bookings - Client: '" + clientName + "', Phone: '" + phone + "'");
        List<CourseBooking> bookings = courseBookingRepository.findByClientNameAndPhone(clientName, phone);
        System.out.println("Found " + bookings.size() + " bookings");
        return bookings;
    }

    public List<CourseBooking> getCourseBookingsByParlour(UUID parlourId) {
        return courseBookingRepository.findByParlourId(parlourId);
    }

    public void cancelCourseBooking(UUID bookingId) {
        CourseBooking booking = courseBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Course booking not found"));
        
        if (booking.getStatus() != CourseBooking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be cancelled");
        }
        
        booking.setStatus(CourseBooking.BookingStatus.CANCELLED);
        booking.setCancelReason("Cancelled by client");
        courseBookingRepository.save(booking);
    }

    public CourseBooking updateCourseBookingStatus(UUID bookingId, UpdateBookingStatusRequest request, UUID parlourId) {
        CourseBooking booking = courseBookingRepository.findByIdAndParlourId(bookingId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Course booking not found"));

        CourseBooking.BookingStatus status = CourseBooking.BookingStatus.valueOf(request.getStatus().toUpperCase());
        booking.setStatus(status);
        
        if (status == CourseBooking.BookingStatus.CANCELLED && request.getCancelReason() != null) {
            booking.setCancelReason(request.getCancelReason());
        }

        return courseBookingRepository.save(booking);
    }

    // Service Booking Methods
    public ServiceBooking bookService(BookServiceRequest request) {
        // Verify service exists
        com.beautyparlour.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        // Validate phone number
        String phone = validateAndNormalizePhone(request.getPhone());

        ServiceBooking booking = new ServiceBooking(
                service.getParlourId(),
                request.getServiceId(),
                request.getClientName().trim(),
                phone
        );
        return serviceBookingRepository.save(booking);
    }

    public List<ServiceBooking> getServiceBookingsByClient(String clientName, String phone) {
        return serviceBookingRepository.findByClientNameAndPhone(clientName, phone);
    }

    public List<ServiceBooking> getServiceBookingsByParlour(UUID parlourId) {
        return serviceBookingRepository.findByParlourId(parlourId);
    }

    public void cancelServiceBooking(UUID bookingId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service booking not found"));
        
        if (booking.getStatus() != ServiceBooking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be cancelled");
        }
        
        booking.setStatus(ServiceBooking.BookingStatus.CANCELLED);
        booking.setCancelReason("Cancelled by client");
        serviceBookingRepository.save(booking);
    }

    public ServiceBooking updateServiceBookingStatus(UUID bookingId, UpdateBookingStatusRequest request, UUID parlourId) {
        ServiceBooking booking = serviceBookingRepository.findByIdAndParlourId(bookingId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Service booking not found"));

        ServiceBooking.BookingStatus status = ServiceBooking.BookingStatus.valueOf(request.getStatus().toUpperCase());
        booking.setStatus(status);
        
        if (status == ServiceBooking.BookingStatus.CANCELLED && request.getCancelReason() != null) {
            booking.setCancelReason(request.getCancelReason());
        }

        return serviceBookingRepository.save(booking);
    }

    // Helper method to validate and normalize phone numbers
    private String validateAndNormalizePhone(String phone) {
        // Remove any non-digit characters
        String normalized = phone.replaceAll("[^0-9]", "");

        // Check if it's a valid 10-digit number after normalization
        if (normalized.length() != 10) {
            throw new IllegalArgumentException("Phone number must be a 10-digit number");
        }

        return normalized;
    }
}

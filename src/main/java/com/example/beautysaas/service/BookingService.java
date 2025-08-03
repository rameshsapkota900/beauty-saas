package com.example.beautysaas.service;

import com.example.beautysaas.dto.booking.BookingCreateRequest;
import com.example.beautysaas.dto.booking.BookingDto;
import com.example.beautysaas.entity.Booking;
import com.example.beautysaas.entity.Course;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.Service;
import com.example.beautysaas.entity.Staff;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.BookingRepository;
import com.example.beautysaas.repository.CourseRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.ServiceRepository;
import com.example.beautysaas.repository.StaffRepository;
import com.example.beautysaas.repository.UserRepository;
import com.example.beautysaas.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ParlourRepository parlourRepository;
    private final ServiceRepository serviceRepository;
    private final CourseRepository courseRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService; // Inject NotificationService

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ParlourRepository parlourRepository,
                          ServiceRepository serviceRepository,
                          CourseRepository courseRepository,
                          StaffRepository staffRepository,
                          ModelMapper modelMapper,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.parlourRepository = parlourRepository;
        this.serviceRepository = serviceRepository;
        this.courseRepository = courseRepository;
        this.staffRepository = staffRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public BookingDto createServiceBooking(String customerEmail, UUID parlourId, BookingCreateRequest createRequest) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", customerEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Service service = serviceRepository.findById(createRequest.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", createRequest.getItemId()));

        if (!service.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service does not belong to the specified parlour.");
        }

        validateBookingTime(createRequest.getStartTime(), createRequest.getEndTime(), service.getDurationMinutes());
        checkAvailability(parlourId, createRequest.getStaffId(), createRequest.getStartTime(), createRequest.getEndTime());

        Staff staff = null;
        if (createRequest.getStaffId() != null) {
            staff = staffRepository.findById(createRequest.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", createRequest.getStaffId()));
            if (!staff.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
            }
            checkStaffAvailability(staff, createRequest.getStartTime(), createRequest.getEndTime());
        }

        Booking booking = Booking.builder()
                .parlour(parlour)
                .customer(customer)
                .itemId(service.getId())
                .bookingType(Booking.BookingType.SERVICE)
                .startTime(createRequest.getStartTime())
                .endTime(createRequest.getEndTime())
                .staff(staff)
                .price(service.getPrice())
                .status(Booking.BookingStatus.PENDING)
                .notes(createRequest.getNotes())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Service booking created: {}", savedBooking.getId());

        // Send notification
        notificationService.sendBookingConfirmationEmail(customer.getEmail(), customer.getName(), savedBooking);

        return mapToDto(savedBooking);
    }

    @Transactional
    public BookingDto createCourseBooking(String customerEmail, UUID parlourId, BookingCreateRequest createRequest) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", customerEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Course course = courseRepository.findById(createRequest.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", createRequest.getItemId()));

        if (!course.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course does not belong to the specified parlour.");
        }

        validateBookingTime(createRequest.getStartTime(), createRequest.getEndTime(), course.getDurationMinutes());
        checkAvailability(parlourId, createRequest.getStaffId(), createRequest.getStartTime(), createRequest.getEndTime());

        Staff staff = null;
        if (createRequest.getStaffId() != null) {
            staff = staffRepository.findById(createRequest.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", createRequest.getStaffId()));
            if (!staff.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
            }
            checkStaffAvailability(staff, createRequest.getStartTime(), createRequest.getEndTime());
        }

        Booking booking = Booking.builder()
                .parlour(parlour)
                .customer(customer)
                .itemId(course.getId())
                .bookingType(Booking.BookingType.COURSE)
                .startTime(createRequest.getStartTime())
                .endTime(createRequest.getEndTime())
                .staff(staff)
                .price(course.getPrice())
                .status(Booking.BookingStatus.PENDING)
                .notes(createRequest.getNotes())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Course booking created: {}", savedBooking.getId());

        // Send notification
        notificationService.sendBookingConfirmationEmail(customer.getEmail(), customer.getName(), savedBooking);

        return mapToDto(savedBooking);
    }

    public Page<BookingDto> getCustomerBookings(String customerEmail, Booking.BookingType bookingType, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findByCustomerEmailAndBookingType(customerEmail, bookingType, pageable);
        return bookings.map(this::mapToDto);
    }

    public Page<BookingDto> getAllBookingsForAdmin(String adminEmail, Booking.BookingType bookingType, Pageable pageable) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with a parlour.");
        }

        Page<Booking> bookings = bookingRepository.findByParlourIdAndBookingType(admin.getParlour().getId(), bookingType, pageable);
        return bookings.map(this::mapToDto);
    }

    @Transactional
    public BookingDto updateBookingStatus(String adminEmail, UUID bookingId, String status) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with a parlour.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (!booking.getParlour().getId().equals(admin.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Booking does not belong to the admin's parlour.");
        }

        try {
            Booking.BookingStatus newStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            booking.setStatus(newStatus);
            Booking updatedBooking = bookingRepository.save(booking);
            log.info("Booking {} status updated to {} by Admin {}.", bookingId, newStatus, adminEmail);

            // Send notification about status update
            notificationService.sendBookingStatusUpdateEmail(
                    updatedBooking.getCustomer().getEmail(),
                    updatedBooking.getCustomer().getName(),
                    updatedBooking
            );

            return mapToDto(updatedBooking);
        } catch (IllegalArgumentException e) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Invalid booking status: " + status);
        }
    }

    @Transactional
    public void cancelBooking(String userEmail, UUID bookingId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Only customer who made the booking or an admin of the parlour can cancel
        boolean isCustomer = user.getRole().getName().equals("CUSTOMER") && booking.getCustomer().getId().equals(user.getId());
        boolean isAdmin = user.getRole().getName().equals("ADMIN") && user.getParlour() != null && booking.getParlour().getId().equals(user.getParlour().getId());

        if (!isCustomer && !isAdmin) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this booking.");
        }

        if (booking.getStatus().equals(Booking.BookingStatus.COMPLETED) || booking.getStatus().equals(Booking.BookingStatus.CANCELED)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Cannot cancel a completed or already canceled booking.");
        }

        booking.setStatus(Booking.BookingStatus.CANCELED);
        bookingRepository.save(booking);
        log.info("Booking {} canceled by user {}.", bookingId, userEmail);

        // Send notification about cancellation
        notificationService.sendBookingCancellationEmail(
                booking.getCustomer().getEmail(),
                booking.getCustomer().getName(),
                booking
        );
    }

    private BookingDto mapToDto(Booking booking) {
        BookingDto dto = modelMapper.map(booking, BookingDto.class);
        dto.setCustomerName(booking.getCustomer().getName());
        dto.setCustomerEmail(booking.getCustomer().getEmail());
        if (booking.getStaff() != null) {
            dto.setStaffName(booking.getStaff().getName());
        }
        // Set item name based on booking type
        if (booking.getBookingType() == Booking.BookingType.SERVICE) {
            serviceRepository.findById(booking.getItemId()).ifPresent(s -> dto.setItemName(s.getName()));
        } else if (booking.getBookingType() == Booking.BookingType.COURSE) {
            courseRepository.findById(booking.getItemId()).ifPresent(c -> dto.setItemName(c.getName()));
        }
        return dto;
    }

    /**
     * Validates if the booking start and end times are valid and consistent with the item's duration.
     */
    private void validateBookingTime(LocalDateTime startTime, LocalDateTime endTime, Integer durationMinutes) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Booking end time must be after start time.");
        }
        long actualDuration = java.time.Duration.between(startTime, endTime).toMinutes();
        if (actualDuration != durationMinutes) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST,
                    String.format("Booking duration (%d minutes) does not match item's required duration (%d minutes).", actualDuration, durationMinutes));
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Booking cannot be in the past.");
        }
    }

    /**
     * Checks for general parlour availability and specific staff availability.
     */
    private void checkAvailability(UUID parlourId, UUID staffId, LocalDateTime startTime, LocalDateTime endTime) {
        // Check for general parlour availability conflicts (e.g., if the parlour has limited capacity for concurrent bookings)
        // This is a simplified check. A more robust system might involve resource management.
        List<Booking> parlourConflicts = bookingRepository.findConflictingBookingsForParlour(parlourId, startTime, endTime);
        if (!parlourConflicts.isEmpty()) {
            // You might want to be more specific here, e.g., check if the conflict is for the same service/course type
            // or if it exceeds a general concurrent booking limit for the parlour.
            // For now, a simple overlap check is sufficient.
            throw new BeautySaasApiException(HttpStatus.CONFLICT, "The parlour is not available at the requested time due to existing bookings.");
        }

        if (staffId != null) {
            // Specific staff availability check is handled by checkStaffAvailability
        }
    }

    /**
     * Checks if the requested staff member is available at the given time.
     */
    private void checkStaffAvailability(Staff staff, LocalDateTime startTime, LocalDateTime endTime) {
        // Check if booking time is within staff's general working hours
        LocalTime bookingStartTime = startTime.toLocalTime();
        LocalTime bookingEndTime = endTime.toLocalTime();

        if (bookingStartTime.isBefore(staff.getAvailableStartTime()) || bookingEndTime.isAfter(staff.getAvailableEndTime())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff is not available outside their working hours.");
        }

        // Check for overlapping bookings for the specific staff member
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookingsForStaff(staff.getId(), startTime, endTime);
        if (!conflictingBookings.isEmpty()) {
            throw new BeautySaasApiException(HttpStatus.CONFLICT, "Staff member is already booked at the requested time.");
        }
    }
}

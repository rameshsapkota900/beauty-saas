package com.beautyparlour.dto.response;

import com.beautyparlour.entity.CourseBooking;

import java.time.LocalDateTime;
import java.util.UUID;

public class CourseBookingDTO {
    private UUID id;
    private UUID parlourId;
    private UUID courseId;
    private String clientName;
    private String phone;
    private CourseBooking.BookingStatus status;
    private String cancelReason;
    private LocalDateTime createdAt;
    private CourseDTO course;

    public CourseBookingDTO() {
    }

    public CourseBookingDTO(CourseBooking booking) {
        this.id = booking.getId();
        this.parlourId = booking.getParlourId();
        this.courseId = booking.getCourseId();
        this.clientName = booking.getClientName();
        this.phone = booking.getPhone();
        this.status = booking.getStatus();
        this.cancelReason = booking.getCancelReason();
        this.createdAt = booking.getCreatedAt();

        if (booking.getCourse() != null) {
            this.course = new CourseDTO(booking.getCourse());
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParlourId() {
        return parlourId;
    }

    public void setParlourId(UUID parlourId) {
        this.parlourId = parlourId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CourseBooking.BookingStatus getStatus() {
        return status;
    }

    public void setStatus(CourseBooking.BookingStatus status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }
}

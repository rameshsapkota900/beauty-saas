package com.beautyparlour.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_bookings")
public class ServiceBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parlour_id", nullable = false)
    private UUID parlourId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @NotBlank
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @NotBlank
    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    public enum BookingStatus {
        PENDING, ACCEPTED, CANCELLED, COMPLETED
    }

    // Constructors
    public ServiceBooking() {}

    public ServiceBooking(UUID parlourId, UUID serviceId, String clientName, String phone) {
        this.parlourId = parlourId;
        this.serviceId = serviceId;
        this.clientName = clientName;
        this.phone = phone;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getParlourId() { return parlourId; }
    public void setParlourId(UUID parlourId) { this.parlourId = parlourId; }

    public UUID getServiceId() { return serviceId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}

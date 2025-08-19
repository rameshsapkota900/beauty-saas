package com.beautyparlour.dto.response;

import com.beautyparlour.entity.ServiceBooking;

import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceBookingDTO {
    private UUID id;
    private UUID parlourId;
    private UUID serviceId;
    private String clientName;
    private String phone;
    private ServiceBooking.BookingStatus status;
    private String cancelReason;
    private LocalDateTime createdAt;
    
    // Service details
    private String serviceName;
    private String serviceImageUrl;

    public ServiceBookingDTO() {
    }

    public ServiceBookingDTO(ServiceBooking serviceBooking) {
        this.id = serviceBooking.getId();
        this.parlourId = serviceBooking.getParlourId();
        this.serviceId = serviceBooking.getServiceId();
        this.clientName = serviceBooking.getClientName();
        this.phone = serviceBooking.getPhone();
        this.status = serviceBooking.getStatus();
        this.cancelReason = serviceBooking.getCancelReason();
        this.createdAt = serviceBooking.getCreatedAt();

        // Safely get service details only if service is loaded
        if (serviceBooking.getService() != null) {
            this.serviceName = serviceBooking.getService().getName();
            this.serviceImageUrl = serviceBooking.getService().getImageUrl();
        }
    }

    // Getters and setters
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

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
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

    public ServiceBooking.BookingStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBooking.BookingStatus status) {
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceImageUrl() {
        return serviceImageUrl;
    }

    public void setServiceImageUrl(String serviceImageUrl) {
        this.serviceImageUrl = serviceImageUrl;
    }
}

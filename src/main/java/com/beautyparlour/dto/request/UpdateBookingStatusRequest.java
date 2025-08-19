package com.beautyparlour.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateBookingStatusRequest {
    @NotNull(message = "Status is required")
    private String status;

    private String cancelReason;

    // Constructors
    public UpdateBookingStatusRequest() {}

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}

package com.beautyparlour.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event published when a new booking is created
 */
public class BookingCreatedEvent extends ApplicationEvent {
    private final UUID bookingId;
    private final UUID parlourId;
    private final String bookingType; // "SERVICE" or "COURSE"
    private final String clientName;
    private final String clientPhone;

    public BookingCreatedEvent(Object source, UUID bookingId, UUID parlourId, 
                              String bookingType, String clientName, String clientPhone) {
        super(source);
        this.bookingId = bookingId;
        this.parlourId = parlourId;
        this.bookingType = bookingType;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getParlourId() {
        return parlourId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }
}

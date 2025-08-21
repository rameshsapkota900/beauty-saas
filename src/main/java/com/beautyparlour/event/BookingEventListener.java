package com.beautyparlour.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for booking-related events
 */
@Component
public class BookingEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingEventListener.class);

    @EventListener
    @Async
    public void handleBookingCreated(BookingCreatedEvent event) {
        logger.info("New {} booking created: ID={}, Parlour={}, Client={}, Phone={}", 
                   event.getBookingType(), event.getBookingId(), event.getParlourId(), 
                   event.getClientName(), event.getClientPhone());
        
        // Here you can add:
        // - Send notification emails
        // - Update analytics
        // - Send SMS confirmations
        // - Log to external systems
        // - Update dashboards
    }
}

package com.example.busticketbooking.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    RESERVATION_CONFIRMED("Reservation Confirmed",
            "Dear Passenger,\n\nYour reservation has been confirmed. Thank you for choosing our service.\n\nBest regards,\nBus Ticket Booking Team"),
    ;

    private final String subject;
    private final String bodyTemplate;
}


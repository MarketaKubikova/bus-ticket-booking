package com.example.busticketbooking.trip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ScheduledTripResponse(
        String busNumber,
        String origin,
        String destination,
        LocalDateTime departureDateTime,
        LocalDateTime arrivalDateTime,
        int availableSeats,
        BigDecimal priceCzk
) {
}

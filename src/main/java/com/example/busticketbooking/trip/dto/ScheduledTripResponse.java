package com.example.busticketbooking.trip.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ScheduledTripResponse(
        String busNumber,
        String origin,
        String destination,
        ZonedDateTime departureDateTime,
        ZonedDateTime arrivalDateTime,
        int availableSeats,
        BigDecimal priceCzk
) {
}

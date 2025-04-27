package com.example.busticketbooking.trip.dto;

import com.example.busticketbooking.trip.seat.dto.SeatResponse;

import java.time.LocalDateTime;
import java.util.Set;

public record ScheduledTripResponse(
        String busNumber,
        String origin,
        String destination,
        LocalDateTime departureDateTime,
        LocalDateTime arrivalDateTime,
        Set<SeatResponse> seats
) {
}

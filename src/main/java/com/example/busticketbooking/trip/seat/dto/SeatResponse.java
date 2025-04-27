package com.example.busticketbooking.trip.seat.dto;

import com.example.busticketbooking.trip.seat.model.SeatStatus;

public record SeatResponse(
        int seatNumber,
        SeatStatus status
) {
}

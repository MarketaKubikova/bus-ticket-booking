package com.example.busticketbooking.bus.seat.dto;

public record SeatResponse(
        int seatNumber,
        boolean isAvailable
) {
}

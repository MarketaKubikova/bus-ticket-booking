package com.example.busticketbooking.bus.dto;

import com.example.busticketbooking.bus.seat.dto.SeatResponse;

import java.util.Set;

public record BusResponse(
        String busNumber,
        int capacity,
        Set<SeatResponse> seats
) {
}

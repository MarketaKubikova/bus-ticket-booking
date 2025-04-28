package com.example.busticketbooking.trip.route.dto;

import java.time.Duration;

public record RouteResponse(
        String origin,
        String destination,
        Double distance,
        Duration duration
) {
}

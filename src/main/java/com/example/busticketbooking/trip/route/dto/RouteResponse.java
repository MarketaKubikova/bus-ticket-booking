package com.example.busticketbooking.trip.route.dto;

import java.math.BigDecimal;
import java.time.Duration;

public record RouteResponse(
        String origin,
        String destination,
        Double distance,
        Duration duration,
        BigDecimal basePriceCzk
) {
}

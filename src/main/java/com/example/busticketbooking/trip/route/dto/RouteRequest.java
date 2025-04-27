package com.example.busticketbooking.trip.route.dto;

import jakarta.validation.constraints.NotBlank;

public record RouteRequest(
        @NotBlank
        String origin,
        @NotBlank
        String destination
) {
}

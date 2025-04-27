package com.example.busticketbooking.trip.route.city.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CityRequest(
        @NotNull
        @NotBlank
        String name
) {
}

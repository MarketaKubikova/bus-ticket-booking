package com.example.busticketbooking.trip.route.city.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Response object for city details",
        requiredProperties = {"name"})
public record CityResponse(
        @NotNull
        @Schema(description = "Name of the city", example = "Prague")
        String name
) {
}

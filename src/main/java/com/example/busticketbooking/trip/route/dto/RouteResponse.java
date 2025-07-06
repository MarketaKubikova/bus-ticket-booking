package com.example.busticketbooking.trip.route.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Duration;

@Schema(
        description = "Response object for route details",
        requiredProperties = {"origin", "destination", "distance", "duration", "basePriceCzk"})
public record RouteResponse(
        @NotNull
        @Schema(description = "Origin of the route", example = "Prague")
        String origin,
        @NotNull
        @Schema(description = "Destination of the route", example = "Vienna")
        String destination,
        @NotNull
        @Schema(description = "Distance of the route in kilometers", example = "250.0")
        Double distance,
        @NotNull
        @Schema(type = "string", pattern = "^(PT)(\\d{1,2})H(\\d{1,2})(M)", example = "PT2H30M", description = "Duration in hours and minutes")
        Duration duration,
        @NotNull
        @Schema(description = "Base price of the route in CZK", example = "300.00")
        BigDecimal basePriceCzk
) {
}

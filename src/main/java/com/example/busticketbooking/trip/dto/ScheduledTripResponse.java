package com.example.busticketbooking.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Schema(
        description = "Response object for scheduled trip details",
        requiredProperties = {"busNumber", "origin", "destination", "departureDateTime", "arrivalDateTime", "availableSeats", "priceCzk"}
)
public record ScheduledTripResponse(
        @NotNull
        @Schema(description = "Unique identifier for the bus", example = "BUS123")
        String busNumber,
        @NotNull
        @Schema(description = "Origin of the trip", example = "Prague")
        String origin,
        @NotNull
        @Schema(description = "Destination of the trip", example = "Vienna")
        String destination,
        @NotNull
        @Schema(description = "Departure date and time of the trip", example = "2023-10-01T10:00:00Z")
        ZonedDateTime departureDateTime,
        @NotNull
        @Schema(description = "Arrival date and time of the trip", example = "2023-10-01T12:00:00Z")
        ZonedDateTime arrivalDateTime,
        @Schema(
                description = "Number of available seats for the trip",
                example = "30",
                minimum = "0"
        )
        int availableSeats,
        @NotNull
        @Schema(description = "Price of the trip in CZK", example = "500.00")
        BigDecimal priceCzk
) {
}

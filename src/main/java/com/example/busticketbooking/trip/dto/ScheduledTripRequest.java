package com.example.busticketbooking.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Schema(
        description = "Request object for scheduling a trip",
        requiredProperties = {"busNumber", "origin", "destination", "departureTime", "departureDay", "fromDate", "toDate"}
)
public record ScheduledTripRequest(
        @NotBlank
        @Schema(description = "Unique identifier for the bus", example = "BUS123")
        String busNumber,
        @NotBlank
        @Schema(description = "Origin location of the trip", example = "Prague")
        String origin,
        @NotBlank
        @Schema(description = "Destination location of the trip", example = "Vienna")
        String destination,
        @NotNull
        @Schema(description = "Departure time of the trip at departure's city time zone", example = "08:30")
        LocalTime departureTime,
        @NotEmpty
        @Schema(description = "Days of the week when the trip is scheduled")
        Set<DayOfWeek> departureDay,
        @NotNull
        @Schema(description = "Start date for the trip schedule in ISO format", example = "2023-10-01")
        LocalDate fromDate,
        @NotNull
        @Schema(description = "End date for the trip schedule in ISO format", example = "2023-12-31")
        LocalDate toDate
) {
}

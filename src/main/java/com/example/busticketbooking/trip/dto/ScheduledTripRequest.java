package com.example.busticketbooking.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ScheduledTripRequest(
        @NotBlank
        String busNumber,
        @NotBlank
        String origin,
        @NotBlank
        String destination,
        @NotNull
        LocalTime departureTime,
        @NotEmpty
        Set<DayOfWeek> departureDay,
        @NotNull
        LocalDate fromDate,
        @NotNull
        LocalDate toDate
) {
}

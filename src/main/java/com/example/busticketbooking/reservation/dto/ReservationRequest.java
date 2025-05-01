package com.example.busticketbooking.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        @NotNull
        Long scheduledTripId,
        @NotNull
        @Min(1)
        int seatNumber,
        @Email
        String passengerEmail
) {
    public ReservationRequest(Long scheduledTripId, int seatNumber) {
        this(scheduledTripId, seatNumber, "");
    }
}

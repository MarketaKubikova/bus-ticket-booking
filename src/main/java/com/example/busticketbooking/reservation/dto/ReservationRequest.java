package com.example.busticketbooking.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        Long scheduledTripId,
        int seatNumber,
        @NotNull
        @NotBlank
        @NotEmpty
        @Email
        String passengerEmail
) {
}

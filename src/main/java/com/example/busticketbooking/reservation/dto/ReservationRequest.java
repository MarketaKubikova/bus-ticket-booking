package com.example.busticketbooking.reservation.dto;

import com.example.busticketbooking.reservation.model.Tariff;
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
        String passengerEmail,
        Tariff tariff
) {
    public ReservationRequest(Long scheduledTripId, int seatNumber, Tariff tariff) {
        this(scheduledTripId, seatNumber, "", tariff);
    }
}

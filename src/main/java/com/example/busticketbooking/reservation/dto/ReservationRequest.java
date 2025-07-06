package com.example.busticketbooking.reservation.dto;

import com.example.busticketbooking.reservation.model.Tariff;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request object for creating a reservation",
        requiredProperties = {"scheduledTripId", "seatNumber", "tariff"}
)
public record ReservationRequest(
        @NotNull
        @Schema(
                description = "Unique identifier for the scheduled trip",
                example = "12345"
        )
        Long scheduledTripId,
        @NotNull
        @Min(1)
        @Schema(
                description = "Seat number for the reservation",
                example = "12"
        )
        int seatNumber,
        @Email
        @Schema(
                description = "Email address of the passenger",
                example = "joe@doe.com"
        )
        String passengerEmail,
        @NotNull
        @Schema(
                description = "Tariff details for the reservation"
        )
        Tariff tariff
) {
    public ReservationRequest(Long scheduledTripId, int seatNumber, Tariff tariff) {
        this(scheduledTripId, seatNumber, "", tariff);
    }
}

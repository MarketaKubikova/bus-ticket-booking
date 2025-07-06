package com.example.busticketbooking.trip.seat.dto;

import com.example.busticketbooking.trip.seat.model.SeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Response object for seat details",
        requiredProperties = {"seatNumber", "status"}
)
public record SeatResponse(
        @Schema(description = "Unique identifier for the seat", example = "12")
        int seatNumber,
        @NotNull
        @Schema(description = "Current status of the seat")
        SeatStatus status
) {
}

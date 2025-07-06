package com.example.busticketbooking.reservation.dto;

import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.model.Tariff;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@Schema(
        description = "Response object for reservation details",
        requiredProperties = {"origin", "destination", "departureDateTime", "seatNumber", "passengerEmail", "status", "priceCzk", "tariff"})
public class ReservationResponse {
    @NotNull
    @Schema(description = "Origin of the trip", example = "Prague")
    private String origin;
    @NotNull
    @Schema(description = "Destination of the trip", example = "Vienna")
    private String destination;
    @NotNull
    @Schema(description = "Departure date and time of the trip", example = "2023-10-01T10:00:00Z")
    private ZonedDateTime departureDateTime;
    @NotNull
    @Schema(description = "Seat number for the reservation", example = "12")
    private int seatNumber;
    @NotNull
    @Schema(description = "Email address of the passenger", example = "joe@doe.com")
    private String passengerEmail;
    @NotNull
    @Schema(description = "Status of the reservation")
    private ReservationStatus status;
    @NotNull
    @Schema(description = "Price of the reservation in CZK", example = "500.00")
    private BigDecimal priceCzk;
    @NotNull
    @Schema(description = "Tariff details for the reservation")
    private Tariff tariff;
}

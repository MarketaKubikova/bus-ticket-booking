package com.example.busticketbooking.trip.route.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;

@Schema(
        description = "Request object for creating or updating a route",
        requiredProperties = {"origin", "destination", "distance", "duration", "basePriceCzk"})
public record RouteRequest(
        @NotBlank
        @Schema(description = "Origin city of the route", example = "Prague")
        String origin,
        @NotBlank
        @Schema(description = "Destination city of the route", example = "Vienna")
        String destination,
        @Min(value = 0, message = "Distance must be greater than 0")
        @Description("Distance in kilometers")
        @Schema(description = "Distance of the route in kilometers", example = "250.0")
        Double distance,
        @Schema(type = "string", pattern = "^(\\d{2}:(\\d{2})$", example = "02:30", description = "Duration in hours and minutes")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        String duration,
        @NotNull
        @Min(value = 0, message = "Base price must be greater than 0")
        @Schema(description = "Base price of the route in CZK", example = "300.00")
        BigDecimal basePriceCzk
) {
}

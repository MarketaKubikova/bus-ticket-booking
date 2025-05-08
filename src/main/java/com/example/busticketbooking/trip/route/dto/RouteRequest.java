package com.example.busticketbooking.trip.route.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;

public record RouteRequest(
        @NotBlank
        String origin,
        @NotBlank
        String destination,
        @Min(value = 0, message = "Distance must be greater than 0")
        @Description("Distance in kilometers")
        Double distance,
        @Schema(type = "string", pattern = "^(\\d{2}:(\\d{2})$", example = "02:30", description = "Duration in hours and minutes")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        String duration,
        @NotNull
        @Min(value = 0, message = "Base price must be greater than 0")
        BigDecimal basePriceCzk
) {
}

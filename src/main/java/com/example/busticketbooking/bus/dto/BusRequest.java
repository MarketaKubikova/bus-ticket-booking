package com.example.busticketbooking.bus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request object for creating or updating a bus",
        requiredProperties = {"busNumber", "capacity"}
)
public record BusRequest(
        @NotBlank
        @Schema(description = "Unique identifier for the bus", example = "BUS123")
        String busNumber,
        @NotNull
        @Min(1)
        @Schema(description = "Capacity of the bus", example = "50")
        int capacity
) {
}

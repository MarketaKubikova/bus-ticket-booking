package com.example.busticketbooking.bus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Response object for bus details",
        requiredProperties = {"busNumber", "capacity"}
)
public record BusResponse(
        @NotNull
        @Schema(description = "Unique identifier for the bus", example = "BUS123")
        String busNumber,
        @NotNull
        @Schema(description = "Capacity of the bus", example = "50")
        int capacity
) {
}

package com.example.busticketbooking.bus.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusRequest(
        @NotBlank
        String busNumber,
        @NotNull
        @Min(1)
        int capacity
) {
}

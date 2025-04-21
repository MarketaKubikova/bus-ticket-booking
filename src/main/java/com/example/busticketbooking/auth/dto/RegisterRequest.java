package com.example.busticketbooking.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}

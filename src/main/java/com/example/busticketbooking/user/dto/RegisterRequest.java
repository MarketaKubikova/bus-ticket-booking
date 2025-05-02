package com.example.busticketbooking.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}

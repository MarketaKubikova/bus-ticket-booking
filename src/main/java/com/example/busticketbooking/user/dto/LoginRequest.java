package com.example.busticketbooking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        description = "Request object for user login",
        requiredProperties = {"username", "password"}
)
public record LoginRequest(
        @NotBlank
        @Schema(description = "Username of the user", example = "john@doe.com")
        String username,
        @NotBlank
        @Schema(description = "Password of the user", example = "securePassword123")
        String password
) {
}


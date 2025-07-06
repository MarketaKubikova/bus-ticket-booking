package com.example.busticketbooking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        description = "Request object for user registration",
        requiredProperties = {"email", "password"}
)
public record RegisterRequest(
        @NotBlank
        @Schema(description = "Email of the user", example = "joe@doe.com")
        String email,
        @NotBlank
        @Schema(description = "Password of the user", example = "securePassword123")
        String password
) {
}

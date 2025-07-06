package com.example.busticketbooking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Response object for user authentication",
        requiredProperties = {"token"}
)
public record AuthResponse(
        @NotNull
        @Schema(description = "JWT token for authenticated user", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}


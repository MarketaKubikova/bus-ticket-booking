package com.example.busticketbooking.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Response object for payment processing",
        requiredProperties = {"message"})
public record PaymentResponse(
        @NotNull
        @Schema(description = "Message indicating the result of the payment processing", example = "Payment successful")
        String message
) {
}

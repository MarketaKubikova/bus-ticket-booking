package com.example.busticketbooking.payment.dto;

import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request object for processing a payment",
        requiredProperties = {"reservationId", "method", "transactionType"})
public record PaymentRequest(
        @NotNull
        @Schema(description = "Unique identifier for the reservation", example = "12345")
        Long reservationId,
        @NotNull
        @Schema(description = "Payment method type")
        PaymentMethodType method,
        @NotNull
        @Schema(description = "Type of transaction")
        TransactionType transactionType,
        @Schema(description = "Coupon code for the payment", example = "DISCOUNT2023")
        String couponCode
) {
}

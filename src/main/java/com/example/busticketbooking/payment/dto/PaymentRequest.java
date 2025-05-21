package com.example.busticketbooking.payment.dto;

import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.TransactionType;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        Long reservationId,
        @NotNull
        PaymentMethodType method,
        @NotNull
        TransactionType transactionType,
        String couponCode
) {
}

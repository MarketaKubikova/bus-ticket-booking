package com.example.busticketbooking.payment.controller;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
@Tag(name = "Payment Processing", description = "Operations related to payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            operationId = "PayReservation",
            summary = "Process payment for a reservation",
            security = @SecurityRequirement(name = ""),
            description = "Processes the payment for a reservation using the provided payment details.",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payment request containing payment method and transaction details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentRequest.class)
                    ),
                    required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Payment processed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid payment request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid payment request")
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PaymentResponse> payReservation(@RequestBody @Valid PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.processPayment(paymentRequest));
    }
}

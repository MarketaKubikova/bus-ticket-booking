package com.example.busticketbooking.payment.controller;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.service.WalletRechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Validated
@Tag(name = "Wallet Management", description = "Operations related to wallet management")
public class WalletController {
    private final WalletRechargeService walletRechargeService;

    @Operation(
            operationId = "RechargeWallet",
            summary = "Recharge user wallet",
            description = "Allows users to recharge their wallet using various payment methods.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
                            description = "Wallet recharged successfully",
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
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have permission to recharge wallet",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "You do not have permission to perform this action")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Coupon not found or expired",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Coupon with code {couponCode} not found or expired")
                            )
                    )
            }
    )
    @PostMapping("/recharge")
    public ResponseEntity<PaymentResponse> rechargeWallet(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(walletRechargeService.recharge(request));
    }
}

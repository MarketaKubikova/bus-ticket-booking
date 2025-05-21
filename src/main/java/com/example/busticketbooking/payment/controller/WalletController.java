package com.example.busticketbooking.payment.controller;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.service.WalletRechargeService;
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
public class WalletController {
    private final WalletRechargeService walletRechargeService;

    @PostMapping("/recharge")
    public ResponseEntity<PaymentResponse> rechargeWallet(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(walletRechargeService.recharge(request));
    }
}

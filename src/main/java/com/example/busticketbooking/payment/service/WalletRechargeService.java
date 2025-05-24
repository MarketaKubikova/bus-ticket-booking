package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.entity.Coupon;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.PaymentStatus;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.payment.repository.CouponRepository;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.shared.exception.ExpiredCouponCodeException;
import com.example.busticketbooking.shared.exception.ForbiddenException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.service.TransactionHandler;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletRechargeService {

    private final UserService userService;
    private final CouponRepository couponRepository;
    private final WalletService walletService;
    private final PaymentTransactionRepository transactionRepository;
    private final TransactionHandler transactionHandler;

    public PaymentResponse recharge(PaymentRequest request) {
        AppUser user = Optional.ofNullable(userService.getCurrentAuthenticatedUser())
                .orElseThrow(() -> new ForbiddenException("No authenticated user"));

        if (request.method().equals(PaymentMethodType.COUPON)) {
            return transactionHandler.runInTransaction(() -> rechargeWithCoupon(request, user));
        } else {
            throw new IllegalArgumentException("Payment method not supported.");
        }
    }

    private PaymentResponse rechargeWithCoupon(PaymentRequest request, @NotNull AppUser user) {
        Coupon coupon = couponRepository.findByCode(request.couponCode())
                .orElseThrow(() -> new NotFoundException("Coupon with code " + request.couponCode() + " not found"));

        if (!coupon.isValid()) {
            throw new ExpiredCouponCodeException(coupon.getCode());
        }

        Wallet wallet = user.getWallet();
        wallet.increaseBalance(coupon.getAmount());
        walletService.saveWallet(wallet);

        coupon.increaseUsedCount();
        couponRepository.save(coupon);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setWallet(wallet);
        transaction.setAmount(coupon.getAmount());
        transaction.setTransactionType(TransactionType.WALLET_RECHARGE);
        transaction.setPaymentMethod(PaymentMethodType.COUPON);
        transaction.setReference("Coupon: " + request.couponCode());
        transaction.setStatus(PaymentStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return new PaymentResponse("Wallet successfully recharged.");
    }
}

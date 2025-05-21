package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.entity.Coupon;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.payment.repository.CouponRepository;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.shared.exception.ExpiredCouponCodeException;
import com.example.busticketbooking.shared.exception.ForbiddenException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.service.TransactionHandler;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletRechargeServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private WalletService walletService;
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    @Spy
    private TransactionHandler transactionHandler;
    @InjectMocks
    private WalletRechargeService walletRechargeService;

    @Test
    void recharge_withCoupon_validRequest_shouldReturnPaymentResponse() {
        AppUser user = new AppUser();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100L));
        wallet.setUser(user);
        user.setWallet(wallet);
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("TESTCOUPON");
        coupon.setAmount(BigDecimal.valueOf(50L));
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.WALLET_RECHARGE, "TESTCOUPON");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(couponRepository.findByCode("TESTCOUPON")).thenReturn(Optional.of(coupon));
        doNothing().when(walletService).saveWallet(wallet);
        when(couponRepository.save(coupon)).thenReturn(coupon);
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(new PaymentTransaction());

        PaymentResponse response = walletRechargeService.recharge(request);

        assertThat(response).isNotNull();
    }

    @Test
    void recharge_withCoupon_noAuthenticatedUser_shouldThrowForbiddenException() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.WALLET_RECHARGE, "TESTCOUPON");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(null);

        assertThrows(ForbiddenException.class, () -> walletRechargeService.recharge(request));
    }

    @Test
    void recharge_withCoupon_invalidCoupon_shouldThrowNotFoundException() {
        AppUser user = new AppUser();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100L));
        wallet.setUser(user);
        user.setWallet(wallet);
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.WALLET_RECHARGE, "INVALIDCOUPON");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(couponRepository.findByCode("INVALIDCOUPON")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletRechargeService.recharge(request));
    }

    @Test
    void recharge_withCoupon_expiredCoupon_shouldThrowExpiredCouponCodeException() {
        AppUser user = new AppUser();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100L));
        wallet.setUser(user);
        user.setWallet(wallet);
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("EXPIREDCOUPON");
        coupon.setAmount(BigDecimal.valueOf(50L));
        coupon.setUsageLimit(10L);
        coupon.setUsedCount(10L);
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.WALLET_RECHARGE, "EXPIREDCOUPON");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(couponRepository.findByCode("EXPIREDCOUPON")).thenReturn(Optional.of(coupon));

        assertThrows(ExpiredCouponCodeException.class, () -> walletRechargeService.recharge(request));
    }
}

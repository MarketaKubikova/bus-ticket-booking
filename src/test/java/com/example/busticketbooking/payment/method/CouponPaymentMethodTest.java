package com.example.busticketbooking.payment.method;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.entity.Coupon;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.payment.repository.CouponRepository;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.ExpiredCouponCodeException;
import com.example.busticketbooking.shared.exception.InsufficientBalanceException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.service.DateTimeService;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponPaymentMethodTest {
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private ReservationService reservationService;
    @Mock
    private DateTimeService dateTimeService;
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    @InjectMocks
    private CouponPaymentMethod couponPaymentMethod;

    @Test
    void pay_validCoupon_shouldReturnPaymentResponse() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("TESTCOUPON");
        coupon.setAmount(BigDecimal.TEN);

        Reservation reservation = new Reservation(1L, new ScheduledTrip(), "test@test.com", new Seat(), Instant.parse("2025-05-21T08:33:00Z"), null, ReservationStatus.RESERVED, null, BigDecimal.TEN, Tariff.ADULT, new PaymentTransaction());

        when(couponRepository.findByCode("TESTCOUPON")).thenReturn(Optional.of(coupon));
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.now());
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(new PaymentTransaction());
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        doNothing().when(reservationService).saveReservation(any(Reservation.class));

        PaymentResponse response = couponPaymentMethod.pay(new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.TICKET_PURCHASE, "TESTCOUPON"), reservation);

        assertThat(response.message()).isEqualTo("Payment by coupon successful.");
    }

    @Test
    void pay_invalidCoupon_shouldThrowNotFoundException() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.TICKET_PURCHASE, "INVALIDCOUPON");
        Reservation reservation = new Reservation();

        when(couponRepository.findByCode("INVALIDCOUPON")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> couponPaymentMethod.pay(request, reservation));
    }

    @Test
    void pay_expiredCoupon_shouldThrowNotFoundException() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("EXPIREDCOUPON");
        coupon.setAmount(BigDecimal.TEN);
        coupon.setUsageLimit(10L);
        coupon.setUsedCount(10L);
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.TICKET_PURCHASE, "EXPIREDCOUPON");
        Reservation reservation = new Reservation();

        when(couponRepository.findByCode("EXPIREDCOUPON")).thenReturn(Optional.of(coupon));

        assertThrows(ExpiredCouponCodeException.class, () -> couponPaymentMethod.pay(request, reservation));
    }

    @Test
    void pay_insufficientCouponBalance_shouldThrowInsufficientBalanceException() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("INSUFFICIENTCOUPON");
        coupon.setAmount(BigDecimal.ONE);
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.TICKET_PURCHASE, "INSUFFICIENTCOUPON");
        Reservation reservation = new Reservation(1L, new ScheduledTrip(), "test@test.com", new Seat(), Instant.parse("2025-05-21T12:00:00Z"), null, ReservationStatus.RESERVED, null, BigDecimal.TEN, Tariff.ADULT, new PaymentTransaction());

        when(couponRepository.findByCode("INSUFFICIENTCOUPON")).thenReturn(Optional.of(coupon));

        assertThrows(InsufficientBalanceException.class, () -> couponPaymentMethod.pay(request, reservation));
    }
}

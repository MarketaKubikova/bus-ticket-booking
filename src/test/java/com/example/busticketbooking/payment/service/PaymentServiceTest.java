package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.method.CouponPaymentMethod;
import com.example.busticketbooking.payment.method.PaymentMethodFactory;
import com.example.busticketbooking.payment.method.WalletPaymentMethod;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentMethodFactory methodFactory;
    @Mock
    private CouponPaymentMethod couponPaymentMethod;
    @Mock
    private WalletPaymentMethod walletPaymentMethod;
    @Mock
    private ReservationService reservationService;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_couponMethod_shouldReturnResponse() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.COUPON, TransactionType.TICKET_PURCHASE, "TESTCOUPON");
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.RESERVED);

        when(reservationService.getReservationById(request.reservationId())).thenReturn(reservation);
        when(methodFactory.getStrategy(request.method())).thenReturn(couponPaymentMethod);
        when(couponPaymentMethod.pay(request, reservation)).thenReturn(new PaymentResponse("Coupon payment successful"));

        PaymentResponse response = paymentService.processPayment(request);

        assertThat(response).isNotNull();
    }

    @Test
    void processPayment_walletMethod_shouldReturnResponse() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.WALLET, TransactionType.TICKET_PURCHASE, null);
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.RESERVED);

        when(reservationService.getReservationById(request.reservationId())).thenReturn(reservation);
        when(methodFactory.getStrategy(request.method())).thenReturn(walletPaymentMethod);
        when(walletPaymentMethod.pay(request, reservation)).thenReturn(new PaymentResponse("Wallet payment successful"));

        PaymentResponse response = paymentService.processPayment(request);

        assertThat(response).isNotNull();
    }

    @Test
    void processPayment_invalidMethod_shouldThrowException() {
        PaymentRequest request = new PaymentRequest(1L, null, TransactionType.TICKET_PURCHASE, null);
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.RESERVED);

        when(reservationService.getReservationById(request.reservationId())).thenReturn(reservation);
        when(methodFactory.getStrategy(request.method())).thenThrow(new IllegalArgumentException("Invalid payment method"));

        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(request));
    }

    @Test
    void processPayment_invalidReservationStatus_shouldThrowException() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.WALLET, TransactionType.TICKET_PURCHASE, null);
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EXPIRED);

        when(reservationService.getReservationById(request.reservationId())).thenReturn(reservation);

        assertThrows(BadRequestException.class, () -> paymentService.processPayment(request));
    }
}

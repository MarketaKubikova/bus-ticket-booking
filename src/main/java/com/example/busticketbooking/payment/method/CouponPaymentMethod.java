package com.example.busticketbooking.payment.method;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.entity.Coupon;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.PaymentStatus;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.payment.repository.CouponRepository;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.ExpiredCouponCodeException;
import com.example.busticketbooking.shared.exception.InsufficientBalanceException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponPaymentMethod implements PaymentMethod {
    private final CouponRepository couponRepository;
    private final ReservationService reservationService;
    private final PaymentTransactionRepository transactionRepository;

    @Transactional
    @Override
    public PaymentResponse pay(PaymentRequest request) {
        Coupon coupon = couponRepository.findByCode(request.couponCode())
                .orElseThrow(() -> new NotFoundException("Coupon '" + request.couponCode() + "' not found."));

        if (!coupon.isValid()) {
            throw new ExpiredCouponCodeException(coupon.getCode());
        }

        Reservation reservation = reservationService.getReservationById(request.reservationId());

        if (coupon.getAmount().compareTo(reservation.getPriceCzk()) < 0) {
            throw new InsufficientBalanceException("Coupon balance is insufficient for this reservation.");
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPaymentMethod(PaymentMethodType.COUPON);
        transaction.setTransactionType(TransactionType.TICKET_PURCHASE);
        transaction.setStatus(PaymentStatus.COMPLETED);
        transaction.setReservation(reservation);
        transaction.setAmount(reservation.getPriceCzk());
        transaction.setReference("Coupon: " + coupon.getCode());
        transactionRepository.save(transaction);

        coupon.increaseUsedCount();
        couponRepository.save(coupon);

        reservation.setStatus(ReservationStatus.PAID);
        reservationService.saveReservation(reservation);

        return new PaymentResponse("Payment by coupon successful.");
    }
}

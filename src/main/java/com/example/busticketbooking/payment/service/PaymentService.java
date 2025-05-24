package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.method.PaymentMethod;
import com.example.busticketbooking.payment.method.PaymentMethodFactory;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentMethodFactory methodFactory;
    private final ReservationService reservationService;

    public PaymentResponse processPayment(PaymentRequest request) {
        Reservation reservation = reservationService.getReservationById(request.reservationId());

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            log.error("Reservation with id {} cannot be paid. Current status: {}", request.reservationId(), reservation.getStatus());
            throw new BadRequestException("Reservation with id " + request.reservationId() + " cannot be paid.");
        }

        PaymentMethod strategy = methodFactory.getStrategy(request.method());
        return strategy.pay(request, reservation);
    }
}

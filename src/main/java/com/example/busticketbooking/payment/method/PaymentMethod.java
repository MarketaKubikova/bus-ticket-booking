package com.example.busticketbooking.payment.method;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.reservation.entity.Reservation;

public interface PaymentMethod {
    PaymentResponse pay(PaymentRequest request, Reservation reservation);
}

package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.method.PaymentMethod;
import com.example.busticketbooking.payment.method.PaymentMethodFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentMethodFactory methodFactory;

    public PaymentService(PaymentMethodFactory methodFactory) {
        this.methodFactory = methodFactory;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentMethod strategy = methodFactory.getStrategy(request.method());
        return strategy.pay(request);
    }
}

package com.example.busticketbooking.payment.method;

import com.example.busticketbooking.payment.model.PaymentMethodType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentMethodFactory {

    private final List<PaymentMethod> strategies;

    public PaymentMethod getStrategy(PaymentMethodType method) {
        return strategies.stream()
                .filter(strategy -> PaymentMethodType.fromString(strategy.getClass().getSuperclass().getSimpleName()
                        .replace("PaymentMethod", "")).equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for method: " + method.name()));
    }
}

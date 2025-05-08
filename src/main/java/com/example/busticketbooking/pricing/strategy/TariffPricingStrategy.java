package com.example.busticketbooking.pricing.strategy;

import com.example.busticketbooking.pricing.context.PricingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(1)
public class TariffPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal apply(BigDecimal currentPrice, PricingContext context) {
        return currentPrice.multiply(context.tariff().getDiscount());
    }
}

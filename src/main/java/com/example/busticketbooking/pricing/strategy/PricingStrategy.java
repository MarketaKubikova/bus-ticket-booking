package com.example.busticketbooking.pricing.strategy;

import com.example.busticketbooking.pricing.context.PricingContext;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal apply(BigDecimal currentPrice, PricingContext context);
}

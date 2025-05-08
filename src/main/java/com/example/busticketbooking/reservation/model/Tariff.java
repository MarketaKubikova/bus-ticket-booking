package com.example.busticketbooking.reservation.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum Tariff {
    ADULT(BigDecimal.ONE),
    CHILD(BigDecimal.valueOf(0.5d)),
    STUDENT(BigDecimal.valueOf(0.8d)),
    SENIOR(BigDecimal.valueOf(0.7d)),
    ;

    private final BigDecimal discount;

    Tariff(BigDecimal discount) {
        this.discount = discount;
    }
}

package com.example.busticketbooking.payment.model;

public enum PaymentMethodType {
    COUPON, WALLET;

    public static PaymentMethodType fromString(String method) {
        for (PaymentMethodType type : PaymentMethodType.values()) {
            if (type.name().equalsIgnoreCase(method)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + method);
    }
}

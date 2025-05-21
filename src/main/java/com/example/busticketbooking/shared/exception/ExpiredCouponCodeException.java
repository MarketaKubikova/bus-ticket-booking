package com.example.busticketbooking.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExpiredCouponCodeException extends RuntimeException {
    public ExpiredCouponCodeException(String couponCode) {
        super("Coupon code '" + couponCode + "' has expired.");
    }
}

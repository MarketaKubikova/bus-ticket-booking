package com.example.busticketbooking.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@RequiredArgsConstructor
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "usage_limit")
    private Long usageLimit;
    @Column(name = "used_count")
    private long usedCount = 0L;
    @Column(name = "valid_to")
    private LocalDateTime validTo;

    public boolean isValid() {
        if (validTo != null && validTo.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (usageLimit == null) {
            return true;
        }
        return usedCount < usageLimit;
    }

    public void increaseUsedCount() {
        usedCount++;
    }
}

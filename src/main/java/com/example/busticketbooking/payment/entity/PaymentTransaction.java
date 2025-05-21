package com.example.busticketbooking.payment.entity;

import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.PaymentStatus;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethodType paymentMethod;
    private LocalDateTime timestamp = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String reference;
    @ManyToOne
    private Wallet wallet;
    @ManyToOne
    private Reservation reservation;
}

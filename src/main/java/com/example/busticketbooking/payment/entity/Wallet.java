package com.example.busticketbooking.payment.entity;

import com.example.busticketbooking.shared.exception.InsufficientBalanceException;
import com.example.busticketbooking.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Getter
@Setter
public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    public void increaseBalance(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    public void decreaseBalance(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        } else {
            this.balance = balance.subtract(amount);
        }
    }
}

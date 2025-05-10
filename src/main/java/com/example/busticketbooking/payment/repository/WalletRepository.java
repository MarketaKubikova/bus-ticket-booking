package com.example.busticketbooking.payment.repository;

import com.example.busticketbooking.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
}

package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.repository.WalletRepository;
import com.example.busticketbooking.user.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;

    public Wallet createWallet(AppUser user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        log.info("Creating wallet for user '{}' with zero balance.", user.getId());

        return walletRepository.save(wallet);
    }

    public void saveWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}

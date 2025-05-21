package com.example.busticketbooking.payment.service;

import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.repository.WalletRepository;
import com.example.busticketbooking.user.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private WalletService walletService;

    @Test
    void createWallet_validUser_shouldReturnWallet() {
        AppUser user = new AppUser();
        user.setId(1L);
        Wallet createdWallet = new Wallet();
        createdWallet.setUser(user);

        when(walletRepository.save(any(Wallet.class))).thenReturn(createdWallet);

        Wallet wallet = walletService.createWallet(user);

        assertThat(wallet.getUser()).isEqualTo(user);
        assertThat(wallet.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void saveWallet_validWallet_shouldSaveWallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100L));

        walletService.saveWallet(wallet);

        verify(walletRepository, times(1)).save(wallet);
    }
}

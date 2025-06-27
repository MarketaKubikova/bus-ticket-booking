package com.example.busticketbooking.payment.method;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.payment.service.WalletService;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.ForbiddenException;
import com.example.busticketbooking.shared.exception.InsufficientBalanceException;
import com.example.busticketbooking.shared.service.DateTimeService;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletPaymentMethodTest {
    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;
    @Mock
    private ReservationService reservationService;
    @Mock
    private DateTimeService dateTimeService;
    @Mock
    private PaymentTransactionRepository transactionRepository;
    @InjectMocks
    private WalletPaymentMethod walletPaymentMethod;

    @Test
    void pay_validRequest_shouldReturnPaymentResponse() {
        AppUser user = new AppUser();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100L));
        wallet.setUser(user);
        user.setWallet(wallet);
        Reservation reservation = new Reservation(1L, new ScheduledTrip(), "test@test.com", new Seat(), Instant.parse("2025-01-01T08:00:00Z"), user, ReservationStatus.RESERVED, null, BigDecimal.TEN, Tariff.ADULT, new PaymentTransaction());
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.WALLET, TransactionType.TICKET_PURCHASE, null);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        doNothing().when(walletService).saveWallet(wallet);
        doNothing().when(reservationService).saveReservation(any(Reservation.class));
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.now());
        when(transactionRepository.save(any(PaymentTransaction.class))).thenReturn(new PaymentTransaction());

        PaymentResponse response = walletPaymentMethod.pay(request, reservation);

        assertThat(response.message()).isEqualTo("Payment by wallet successful.");
    }

    @Test
    void pay_noAuthenticatedUser_shouldThrowForbiddenException() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(null);

        assertThrows(ForbiddenException.class, () -> walletPaymentMethod.pay(new PaymentRequest(1L, PaymentMethodType.WALLET, TransactionType.TICKET_PURCHASE, null), new Reservation()));
    }

    @Test
    void pay_insufficientBalance_shouldThrowForbiddenException() {
        AppUser user = new AppUser();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(5L));
        wallet.setUser(user);
        user.setWallet(wallet);
        Reservation reservation = new Reservation(1L, new ScheduledTrip(), "test@test.com", new Seat(), Instant.parse("2025-01-01T08:33:00Z"), user, ReservationStatus.RESERVED, null, BigDecimal.TEN, Tariff.ADULT, new PaymentTransaction());
        PaymentRequest request = new PaymentRequest(1L, PaymentMethodType.WALLET, TransactionType.TICKET_PURCHASE, null);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);

        assertThrows(InsufficientBalanceException.class, () -> walletPaymentMethod.pay(request, reservation));
    }
}

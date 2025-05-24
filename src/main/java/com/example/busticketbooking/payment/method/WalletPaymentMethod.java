package com.example.busticketbooking.payment.method;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.model.PaymentMethodType;
import com.example.busticketbooking.payment.model.PaymentStatus;
import com.example.busticketbooking.payment.model.TransactionType;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.payment.service.WalletService;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.ForbiddenException;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WalletPaymentMethod implements PaymentMethod {

    private final UserService userService;
    private final WalletService walletService;
    private final ReservationService reservationService;
    private final PaymentTransactionRepository transactionRepository;

    @Transactional
    @Override
    public PaymentResponse pay(PaymentRequest request) {
        AppUser user = Optional.ofNullable(userService.getCurrentAuthenticatedUser())
                .orElseThrow(() -> new ForbiddenException("No authenticated user"));

        Wallet wallet = user.getWallet();
        Reservation reservation = reservationService.getReservationById(request.reservationId());

        wallet.decreaseBalance(reservation.getPriceCzk());
        walletService.saveWallet(wallet);

        reservation.setStatus(ReservationStatus.PAID);
        reservationService.saveReservation(reservation);

        PaymentTransaction transaction = reservation.getPaymentTransaction();
        transaction.setPaymentMethod(PaymentMethodType.WALLET);
        transaction.setTransactionType(TransactionType.TICKET_PURCHASE);
        transaction.setStatus(PaymentStatus.COMPLETED);
        transaction.setReference("Wallet: " + wallet.getId());
        transactionRepository.save(transaction);

        return new PaymentResponse("Payment by wallet successful.");
    }
}

package com.example.busticketbooking.scheduling;

import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.service.DateTimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationCleanupSchedulerTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationService reservationService;
    @Mock
    private DateTimeService dateTimeService;
    @InjectMocks
    private ReservationCleanupScheduler reservationCleanupScheduler;

    @Test
    void cancelExpiredReservations_foundExpiredReservations_shouldCancelReservations() {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        Reservation reservation = new Reservation(1L, null, null, null, Instant.parse("2025-01-01T10:24:00Z"), null, ReservationStatus.RESERVED, null, null, null, paymentTransaction);

        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.parse("2025-01-01T10:30:00Z"));
        when(reservationRepository.findAllByStatusAndCreatedAtBefore(any(), any())).thenReturn(List.of(reservation));
        doNothing().when(reservationService).cancelExpiredReservation(any(Reservation.class));

        reservationCleanupScheduler.cancelExpiredReservations();

        verify(reservationService, times(1)).cancelExpiredReservation(any(Reservation.class));
    }

    @Test
    void cancelExpiredReservations_noExpiredReservations_shouldNotCancel() {
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.parse("2025-01-01T10:30:00Z"));
        when(reservationRepository.findAllByStatusAndCreatedAtBefore(any(), any())).thenReturn(List.of());

        reservationCleanupScheduler.cancelExpiredReservations();

        verify(reservationService, never()).cancelExpiredReservation(any(Reservation.class));
    }

}

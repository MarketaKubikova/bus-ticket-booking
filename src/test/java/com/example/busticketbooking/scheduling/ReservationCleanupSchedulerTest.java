package com.example.busticketbooking.scheduling;

import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.util.Constant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private Clock clock;
    @InjectMocks
    private ReservationCleanupScheduler reservationCleanupScheduler;

    @Test
    void cancelExpiredReservations_foundExpiredReservations_shouldCancelReservations() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 1, 1, 10, 30);
        Instant instant = fixedDateTime.atZone(Constant.ZONE_PRAGUE).toInstant();
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        Reservation reservation = new Reservation(1L, null, null, null, LocalDateTime.of(2025, 1, 1, 10, 24), null, ReservationStatus.RESERVED, null, null, null, paymentTransaction);

        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(Constant.ZONE_PRAGUE);
        when(reservationRepository.findAllByStatusAndCreatedAtBefore(any(), any())).thenReturn(List.of(reservation));
        doNothing().when(reservationService).cancelExpiredReservation(any(Reservation.class));

        reservationCleanupScheduler.cancelExpiredReservations();

        verify(reservationService, times(1)).cancelExpiredReservation(any(Reservation.class));
    }

    @Test
    void cancelExpiredReservations_noExpiredReservations_shouldNotCancel() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 1, 1, 10, 30);
        Instant instant = fixedDateTime.atZone(Constant.ZONE_PRAGUE).toInstant();

        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(Constant.ZONE_PRAGUE);
        when(reservationRepository.findAllByStatusAndCreatedAtBefore(any(), any())).thenReturn(List.of());

        reservationCleanupScheduler.cancelExpiredReservations();

        verify(reservationService, never()).cancelExpiredReservation(any(Reservation.class));
    }

}

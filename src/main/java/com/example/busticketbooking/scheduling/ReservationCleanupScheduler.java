package com.example.busticketbooking.scheduling;

import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.service.DateTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final DateTimeService dateTimeService;

    @Value("${reservation.expiry.minutes}")
    private int expiryMinutes;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredReservations() {
        Instant threshold = dateTimeService.getCurrentUtcTime().minusSeconds(expiryMinutes * 60L);
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndCreatedAtBefore(ReservationStatus.RESERVED, threshold);

        log.info("Found {} expired reservations to cancel", expiredReservations.size());

        expiredReservations.forEach(reservationService::cancelExpiredReservation);
    }
}


package com.example.busticketbooking.reservation.service;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.notification.service.NotificationService;
import com.example.busticketbooking.payment.entity.PaymentTransaction;
import com.example.busticketbooking.payment.model.PaymentStatus;
import com.example.busticketbooking.payment.repository.PaymentTransactionRepository;
import com.example.busticketbooking.pricing.service.PricingService;
import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.mapper.ReservationMapper;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.shared.exception.BadRequestException;
import com.example.busticketbooking.shared.exception.ForbiddenException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.SeatNotAvailableException;
import com.example.busticketbooking.shared.service.DateTimeService;
import com.example.busticketbooking.shared.util.Constant;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.service.SeatService;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.service.UserService;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private static final Duration CANCELLATION_TIME_LIMIT_MINUTES = Duration.ofMinutes(30);

    private final ReservationRepository reservationRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final SeatService seatService;
    private final PricingService pricingService;
    private final ReservationMapper reservationMapper;
    private final UserService userService;
    private final DateTimeService dateTimeService;
    private final NotificationService notificationService;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        ScheduledTrip scheduledTrip = scheduledTripRepository.findById(request.scheduledTripId())
                .orElseThrow(() -> new NotFoundException("ScheduledTrip with ID '" + request.scheduledTripId() + "' not found"));

        Seat seat;

        try {
            seat = seatService.reserveSeat(request.seatNumber(), scheduledTrip);
        } catch (OptimisticLockException e) {
            log.error("Seat {} was already reserved by another user", request.seatNumber());
            throw new SeatNotAvailableException(request.seatNumber());
        }

        Instant currentTime = dateTimeService.getCurrentUtcTime();

        Reservation reservation = new Reservation();
        reservation.setScheduledTrip(scheduledTrip);
        reservation.setSeat(seat);
        reservation.setCreatedAt(currentTime);
        reservation.setTariff(request.tariff());

        AppUser currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser != null) {
            reservation.setUser(currentUser);
            reservation.setPassengerEmail(currentUser.getEmail());
        } else {
            if (request.passengerEmail() == null || request.passengerEmail().isBlank()) {
                throw new BadRequestException("Email must be provided for anonymous reservation");
            }
            reservation.setUser(null);
            reservation.setPassengerEmail(request.passengerEmail());
        }

        BigDecimal price = pricingService.calculatePrice(scheduledTrip, currentUser, request.tariff());
        reservation.setPriceCzk(price);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setReservation(reservation);
        transaction.setAmount(price);
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setCreatedAt(currentTime);

        reservation.setPaymentTransaction(transaction);

        Reservation savedReservation = reservationRepository.save(reservation);
        paymentTransactionRepository.save(transaction);

        log.info("Reservation with id '{}' successfully created at '{}'", savedReservation.getId(), savedReservation.getCreatedAt());
        notificationService.notify(NotificationType.RESERVATION_CONFIRMED, savedReservation);
        return reservationMapper.toResponseDto(savedReservation);
    }

    public List<ReservationResponse> getUsersReservations(@NotNull AppUser user) {
        List<Reservation> reservations = reservationRepository.findAllByUser(user);

        if (reservations.isEmpty()) {
            throw new NotFoundException("No reservations found for user with ID '" + user.getId() + "'");
        }

        return reservations.stream()
                .map(reservationMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void cancelReservation(Long reservationId, @NotNull AppUser user) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.getUser().equals(user)) {
            log.error("User with ID '{}' is trying to cancel a reservation that does not belong to them", user.getId());
            throw new ForbiddenException("You are not authorized to cancel this reservation");
        }

        Instant currentTime = dateTimeService.getCurrentUtcTime();
        Instant departureTime = dateTimeService.convertToUtc(reservation.getScheduledTrip().getDepartureDateTime());

        if (dateTimeService.addDurationToUtc(currentTime, CANCELLATION_TIME_LIMIT_MINUTES).isAfter(departureTime)) {
            log.error("User with ID '{}' is trying to cancel a reservation less than {} minutes before the trip", user.getId(), CANCELLATION_TIME_LIMIT_MINUTES.toMinutes());
            throw new BadRequestException("Reservation cannot be canceled less than " + CANCELLATION_TIME_LIMIT_MINUTES.toMinutes() + " minutes before the trip");
        }

        reservation.setStatus(ReservationStatus.CANCELED);
        reservation.setCanceledAt(currentTime);
        reservationRepository.save(reservation);
        seatService.releaseSeat(reservation.getSeat());

        log.info("Reservation with id '{}' successfully cancelled at {}", reservationId, reservation.getCanceledAt());
    }

    @Transactional
    public void cancelExpiredReservation(Reservation reservation) {
        Instant currentTime = dateTimeService.getCurrentUtcTime();

        reservation.setStatus(ReservationStatus.EXPIRED);
        reservation.setCanceledAt(currentTime);
        reservation.getPaymentTransaction().setStatus(PaymentStatus.EXPIRED);
        reservation.getPaymentTransaction().setReference("Reservation expired");
        reservation.getPaymentTransaction().setUpdatedAt(currentTime);
        seatService.releaseSeat(reservation.getSeat());
        reservationRepository.save(reservation);
        paymentTransactionRepository.save(reservation.getPaymentTransaction());

        log.info("Reservation with id '{}' expired and was canceled at {}", reservation.getId(), dateTimeService.convertToZone(reservation.getCanceledAt(), Constant.ZONE_PRAGUE.getId()));
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation with ID '" + reservationId + "' not found"));
    }

    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }
}

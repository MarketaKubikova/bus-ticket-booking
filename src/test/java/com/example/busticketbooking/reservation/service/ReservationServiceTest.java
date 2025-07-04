package com.example.busticketbooking.reservation.service;

import com.example.busticketbooking.bus.entity.Bus;
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
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.shared.exception.BadRequestException;
import com.example.busticketbooking.shared.exception.ForbiddenException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.SeatNotAvailableException;
import com.example.busticketbooking.shared.service.DateTimeService;
import com.example.busticketbooking.shared.util.Constant;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.service.SeatService;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.model.Role;
import com.example.busticketbooking.user.service.UserService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ScheduledTripRepository scheduledTripRepository;
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    @Mock
    private SeatService seatService;
    @Mock
    private PricingService pricingService;
    @Mock
    private UserService userService;
    @Mock
    private DateTimeService dateTimeService;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private ReservationService service;

    @Test
    void createReservation_validRequestNonLoggedInUser_reservationCreated() {
        ReservationRequest request = new ReservationRequest(1L, 1, "test@test.com", Tariff.ADULT);
        Bus bus = new Bus("99", 5);
        ZonedDateTime departureDateTime = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague"));
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), bus, departureDateTime.toLocalDateTime());
        Seat seat = new Seat(1L, 1, SeatStatus.RESERVED, scheduledTrip, 1);
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        Reservation createdReservation = new Reservation(1L, scheduledTrip, "test@test.com", seat, departureDateTime.toInstant(), null, ReservationStatus.RESERVED, null, BigDecimal.TEN, Tariff.ADULT, paymentTransaction);
        ReservationResponse response = new ReservationResponse("Prague", "Vienna", departureDateTime, 1, "test@test.com", ReservationStatus.RESERVED, BigDecimal.TEN, Tariff.ADULT);

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenReturn(seat);
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.now());
        when(userService.getCurrentAuthenticatedUser()).thenReturn(null);
        when(pricingService.calculatePrice(scheduledTrip, null, Tariff.ADULT)).thenReturn(BigDecimal.TEN);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(createdReservation);
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
        doNothing().when(notificationService).notify(NotificationType.RESERVATION_CONFIRMED, createdReservation);
        when(reservationMapper.toResponseDto(createdReservation)).thenReturn(response);

        ReservationResponse result = service.createReservation(request);

        assertThat(result.getOrigin()).isEqualTo("Prague");
        assertThat(result.getDestination()).isEqualTo("Vienna");
        assertThat(result.getDepartureDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague")));
        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getPassengerEmail()).isEqualTo("test@test.com");
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(result.getPriceCzk()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getTariff()).isEqualTo(Tariff.ADULT);
        verify(scheduledTripRepository, times(1)).findById(anyLong());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(seatService, times(1)).reserveSeat(anyInt(), any(ScheduledTrip.class));
        verify(notificationService, times(1)).notify(eq(NotificationType.RESERVATION_CONFIRMED), any(Reservation.class));
        verify(reservationMapper, times(1)).toResponseDto(any(Reservation.class));
    }

    @Test
    void createReservation_validRequestLoggedInUser_reservationCreated() {
        ReservationRequest request = new ReservationRequest(1L, 1, Tariff.ADULT);
        AppUser user = createUser();
        Bus bus = new Bus("99", 5);
        ZonedDateTime departureDateTime = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague"));
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), bus, departureDateTime.toLocalDateTime());
        Seat seat = new Seat(1L, 1, SeatStatus.RESERVED, scheduledTrip, 1);
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 1, 1, 10, 50);
        Instant instant = fixedDateTime.atZone(Constant.ZONE_PRAGUE).toInstant();
        Reservation createdReservation = new Reservation(1L, scheduledTrip, user.getEmail(), seat, instant, user, ReservationStatus.RESERVED, null, BigDecimal.TEN, Tariff.ADULT, paymentTransaction);
        ReservationResponse response = new ReservationResponse("Prague", "Vienna", departureDateTime, 1, "test@test.com", ReservationStatus.RESERVED, BigDecimal.TEN, Tariff.ADULT);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenReturn(seat);
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.now());
        when(pricingService.calculatePrice(scheduledTrip, user, Tariff.ADULT)).thenReturn(BigDecimal.TEN);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(createdReservation);
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
        doNothing().when(notificationService).notify(NotificationType.RESERVATION_CONFIRMED, createdReservation);
        when(reservationMapper.toResponseDto(createdReservation)).thenReturn(response);

        ReservationResponse result = service.createReservation(request);

        assertThat(result.getOrigin()).isEqualTo("Prague");
        assertThat(result.getDestination()).isEqualTo("Vienna");
        assertThat(result.getDepartureDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague")));
        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getPassengerEmail()).isEqualTo("test@test.com");
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(result.getPriceCzk()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getTariff()).isEqualTo(Tariff.ADULT);
    }

    @Test
    void createReservation_scheduledTripDoesNotExist_shouldThrowException() {
        ReservationRequest request = new ReservationRequest(99L, 1, "test@test.com", Tariff.ADULT);

        when(scheduledTripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createReservation(request));
    }

    @Test
    void createReservation_seatNotAvailable_shouldThrowException() {
        ReservationRequest request = new ReservationRequest(1L, 99, "test@test.com", Tariff.ADULT);
        Bus bus = new Bus("99", 5);
        ZonedDateTime departureDateTime = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague"));
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), bus, departureDateTime.toLocalDateTime());

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenThrow(SeatNotAvailableException.class);

        assertThrows(SeatNotAvailableException.class, () -> service.createReservation(request));
    }

    @Test
    void createReservations_twoUsersReserveSameSeat_shouldThrowException() {
        ReservationRequest request = new ReservationRequest(1L, 1, "test@test.com", Tariff.ADULT);
        Bus bus = new Bus("99", 5);
        ZonedDateTime departureDateTime = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague"));
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), bus, departureDateTime.toLocalDateTime());

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenThrow(OptimisticLockException.class);

        assertThrows(SeatNotAvailableException.class, () -> service.createReservation(request));
    }

    @Test
    void getUsersReservations_validUser_reservationsReturned() {
        AppUser user = createUser();

        when(reservationRepository.findAllByUser(user)).thenReturn(List.of(new Reservation()));
        when(reservationMapper.toResponseDto(any(Reservation.class))).thenReturn(new ReservationResponse("Prague", "Vienna", ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0), ZoneId.of("Europe/Prague")), 1, "test@test.com", ReservationStatus.RESERVED, BigDecimal.TEN, Tariff.ADULT));

        var result = service.getUsersReservations(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrigin()).isEqualTo("Prague");
        assertThat(result.getFirst().getDestination()).isEqualTo("Vienna");
        assertThat(result.getFirst().getDepartureDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0), ZoneId.of("Europe/Prague")));
        assertThat(result.getFirst().getSeatNumber()).isEqualTo(1);
        assertThat(result.getFirst().getPassengerEmail()).isEqualTo("test@test.com");
        assertThat(result.getFirst().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(result.getFirst().getPriceCzk()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void getUsersReservations_noReservationsFound_shouldThrowException() {
        AppUser user = createUser();

        when(reservationRepository.findAllByUser(user)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> service.getUsersReservations(user));
    }

    @Test
    void cancelReservation_validRequest_reservationCancelled() {
        AppUser user = createUser();
        Reservation reservation = getReservationFromPragueToVienna(user);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        doNothing().when(seatService).releaseSeat(reservation.getSeat());
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.parse("2025-01-01T08:00:00Z"));
        when(dateTimeService.convertToUtc(any(ZonedDateTime.class))).thenReturn(Instant.parse("2025-01-01T10:00:00Z"));
        when(dateTimeService.addDurationToUtc(any(Instant.class), any(Duration.class))).thenReturn(Instant.parse("2025-01-01T08:30:00Z"));

        service.cancelReservation(1L, user);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        assertThat(reservation.getSeat().getStatus()).isEqualTo(SeatStatus.FREE);
        assertThat(reservation.getCanceledAt()).isEqualTo(Instant.parse("2025-01-01T08:00:00Z"));
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(reservation);
        verify(seatService, times(1)).releaseSeat(reservation.getSeat());
    }

    @Test
    void cancelReservation_reservationNotFound_shouldThrowException() {
        AppUser user = createUser();

        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.cancelReservation(1L, user));
    }

    @Test
    void cancelReservation_userNotAuthorized_shouldThrowException() {
        AppUser user = createUser();
        AppUser anotherUser = new AppUser();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotherUser");

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(anotherUser);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(ForbiddenException.class, () -> service.cancelReservation(1L, user));
    }

    @Test
    void cancelReservation_tooLateForCancellation_shouldThrowException() {
        AppUser user = createUser();
        Reservation reservation = getReservationFromPragueToVienna(user);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.parse("2025-01-01T09:45:00Z"));
        when(dateTimeService.convertToUtc(any(ZonedDateTime.class))).thenReturn(Instant.parse("2025-01-01T10:00:00Z"));
        when(dateTimeService.addDurationToUtc(any(Instant.class), any(Duration.class))).thenReturn(Instant.parse("2025-01-01T10:15:00Z"));

        assertThrows(BadRequestException.class, () -> service.cancelReservation(1L, user));
    }

    @Test
    void getReservationById_validId_reservationReturned() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setPassengerEmail("test@email.com");
        reservation.setStatus(ReservationStatus.RESERVED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = service.getReservationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPassengerEmail()).isEqualTo("test@email.com");
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    void getReservationById_invalidId_shouldThrowException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getReservationById(99L));
    }

    @Test
    void saveReservation_validReservation_reservationSaved() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setPassengerEmail("test@email.com");
        reservation.setStatus(ReservationStatus.RESERVED);

        when(reservationRepository.save(reservation)).thenReturn(reservation);

        service.saveReservation(reservation);

        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void cancelExpiredReservation_validReservation_reservationCancelled() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setSeat(new Seat());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setPaymentTransaction(new PaymentTransaction());

        when(dateTimeService.getCurrentUtcTime()).thenReturn(Instant.parse("2025-01-01T09:50:00Z"));
        doNothing().when(seatService).releaseSeat(any(Seat.class));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(paymentTransactionRepository.save(reservation.getPaymentTransaction())).thenReturn(reservation.getPaymentTransaction());

        service.cancelExpiredReservation(reservation);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(reservation.getCanceledAt()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 10, 50), ZoneId.of("Europe/Prague")).toInstant());
        assertThat(reservation.getPaymentTransaction().getStatus()).isEqualTo(PaymentStatus.EXPIRED);
        verify(seatService, times(1)).releaseSeat(reservation.getSeat());
        verify(reservationRepository, times(1)).save(reservation);
        verify(paymentTransactionRepository, times(1)).save(reservation.getPaymentTransaction());
    }

    private AppUser createUser() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setReservations(new HashSet<>());

        return user;
    }

    private Reservation getReservationFromPragueToVienna(AppUser user) {
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), new Bus("101", 5), LocalDateTime.of(2025, 1, 1, 11, 0));
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setScheduledTrip(scheduledTrip);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setSeat(new Seat(1, scheduledTrip));
        return reservation;
    }
}

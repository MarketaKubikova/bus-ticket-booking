package com.example.busticketbooking.reservation.service;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.mapper.ReservationMapper;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.SeatNotAvailableException;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.service.SeatService;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.model.Role;
import com.example.busticketbooking.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private SeatService seatService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private ReservationService service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReservation_validRequestNonLoggedInUser_reservationCreated() {
        ReservationRequest request = new ReservationRequest(1L, 1, "test@test.com");
        Bus bus = new Bus("99", 5);
        LocalDateTime departureDateTime = LocalDateTime.of(2025, 1, 1, 11, 0, 0);
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna"), 334.0, Duration.ofHours(4)), bus, departureDateTime);
        Seat seat = new Seat(1L, 1, SeatStatus.RESERVED, scheduledTrip);
        Reservation createdReservation = new Reservation(1L, scheduledTrip, "test@test.com", seat, departureDateTime, null);
        ReservationResponse response = new ReservationResponse("Prague", "Vienna", departureDateTime, 1, "test@test.com");

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenReturn(seat);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(createdReservation);
        when(reservationMapper.toResponseDto(createdReservation)).thenReturn(response);

        ReservationResponse result = service.createReservation(request);

        assertThat(result.getOrigin()).isEqualTo("Prague");
        assertThat(result.getDestination()).isEqualTo("Vienna");
        assertThat(result.getDepartureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0, 0));
        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getPassengerEmail()).isEqualTo("test@test.com");
        verify(scheduledTripRepository, times(1)).findById(anyLong());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(seatService, times(1)).reserveSeat(anyInt(), any(ScheduledTrip.class));
        verify(reservationMapper, times(1)).toResponseDto(any(Reservation.class));
    }

    @Test
    void createReservation_validRequestLoggedInUser_reservationCreated() {
        ReservationRequest request = new ReservationRequest(1L, 1);
        AppUser user = createUser();
        Bus bus = new Bus("99", 5);
        LocalDateTime departureDateTime = LocalDateTime.of(2025, 1, 1, 11, 0, 0);
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna"), 334.0, Duration.ofHours(4)), bus, departureDateTime);
        Seat seat = new Seat(1L, 1, SeatStatus.RESERVED, scheduledTrip);
        Reservation createdReservation = new Reservation(1L, scheduledTrip, "test@test.com", seat, departureDateTime, null);
        ReservationResponse response = new ReservationResponse("Prague", "Vienna", departureDateTime, 1, "test@test.com");

        when(authentication.getName()).thenReturn(user.getUsername());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(createdReservation);
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenReturn(seat);
        when(reservationMapper.toResponseDto(createdReservation)).thenReturn(response);

        ReservationResponse result = service.createReservation(request);

        assertThat(result.getOrigin()).isEqualTo("Prague");
        assertThat(result.getDestination()).isEqualTo("Vienna");
        assertThat(result.getDepartureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0, 0));
        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getPassengerEmail()).isEqualTo("test@test.com");
    }

    @Test
    void createReservation_scheduledTripDoesNotExist_shouldThrowException() {
        ReservationRequest request = new ReservationRequest(99L, 1, "test@test.com");

        when(scheduledTripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createReservation(request));
    }

    @Test
    void createReservation_seatNotAvailable_shouldThrowException() {
        ReservationRequest request = new ReservationRequest(1L, 99, "test@test.com");
        Bus bus = new Bus("99", 5);
        LocalDateTime departureDateTime = LocalDateTime.of(2025, 1, 1, 11, 0, 0);
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna"), 334.0, Duration.ofHours(4)), bus, departureDateTime);

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(seatService.reserveSeat(request.seatNumber(), scheduledTrip)).thenThrow(SeatNotAvailableException.class);

        assertThrows(SeatNotAvailableException.class, () -> service.createReservation(request));
    }

    @Test
    void getUsersReservations_validUser_reservationsReturned() {
        AppUser user = createUser();

        when(reservationRepository.findAllByUser(user)).thenReturn(List.of(new Reservation()));
        when(reservationMapper.toResponseDto(any(Reservation.class))).thenReturn(new ReservationResponse("Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0), 1, "test@test.com"));

        var result = service.getUsersReservations(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrigin()).isEqualTo("Prague");
        assertThat(result.getFirst().getDestination()).isEqualTo("Vienna");
        assertThat(result.getFirst().getDepartureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0));
        assertThat(result.getFirst().getSeatNumber()).isEqualTo(1);
        assertThat(result.getFirst().getPassengerEmail()).isEqualTo("test@test.com");
    }

    @Test
    void getUsersReservations_noReservationsFound_shouldThrowException() {
        AppUser user = createUser();

        when(reservationRepository.findAllByUser(user)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> service.getUsersReservations(user));
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
}

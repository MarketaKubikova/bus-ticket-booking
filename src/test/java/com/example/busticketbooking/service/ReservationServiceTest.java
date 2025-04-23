package com.example.busticketbooking.service;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.bus.seat.entity.Seat;
import com.example.busticketbooking.bus.seat.repository.SeatRepository;
import com.example.busticketbooking.common.exception.NotFoundException;
import com.example.busticketbooking.common.exception.SeatNotAvailableException;
import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.mapper.ReservationMapper;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private SeatRepository seatRepository;
    @Mock
    private ReservationMapper reservationMapper;
    @InjectMocks
    private ReservationService service;

    @Test
    void createReservation_validRequest_reservationCreated() {
        ReservationRequest request = new ReservationRequest(1L, 1, "test@test.com");
        Bus bus = new Bus("99", 5);
        LocalDateTime departureDateTime = LocalDateTime.of(2025, 1, 1, 11, 0, 0);
        ScheduledTrip scheduledTrip = new ScheduledTrip(1L, new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna")), bus, departureDateTime);
        Reservation reservation = new Reservation(null, scheduledTrip, "test@test.com", 1, departureDateTime);
        Reservation createdReservation = new Reservation(1L, scheduledTrip, "test@test.com", 1, departureDateTime);
        ReservationResponse response = new ReservationResponse("Prague", "Vienna", departureDateTime, 1, "test@test.com");

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));
        when(reservationMapper.toEntity(request)).thenReturn(reservation);
        when(reservationRepository.save(reservation)).thenReturn(createdReservation);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat(1L, 1, bus, false));
        when(reservationMapper.toResponseDto(createdReservation)).thenReturn(response);

        ReservationResponse result = service.createReservation(request);

        assertThat(result.getOrigin()).isEqualTo("Prague");
        assertThat(result.getDestination()).isEqualTo("Vienna");
        assertThat(result.getDepartureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0, 0));
        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getPassengerEmail()).isEqualTo("test@test.com");
        verify(scheduledTripRepository, times(1)).findById(anyLong());
        verify(reservationMapper, times(1)).toEntity(any(ReservationRequest.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(seatRepository, times(1)).save(any(Seat.class));
        verify(reservationMapper, times(1)).toResponseDto(any(Reservation.class));
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
        ScheduledTrip scheduledTrip = new ScheduledTrip(1L, new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna")), bus, departureDateTime);

        when(scheduledTripRepository.findById(1L)).thenReturn(Optional.of(scheduledTrip));

        assertThrows(SeatNotAvailableException.class, () -> service.createReservation(request));
    }
}

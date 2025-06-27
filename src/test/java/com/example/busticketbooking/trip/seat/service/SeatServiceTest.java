package com.example.busticketbooking.trip.seat.service;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.SeatNotAvailableException;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.seat.dto.SeatResponse;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {
    @Mock
    private SeatRepository seatRepository;
    @InjectMocks
    private SeatService seatService;

    @Test
    void reserveSeat_validSeatNumber_shouldReturnReservedSeat() {
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), new Bus("101", 5), LocalDateTime.of(2025, 1, 1, 8, 0));
        Seat seat = new Seat(1L, 1, SeatStatus.FREE, scheduledTrip, 1);
        scheduledTrip.setSeats(Set.of(seat));

        when(seatRepository.findById(any())).thenReturn(Optional.of(seat));
        when(seatRepository.saveAndFlush(seat)).thenReturn(seat);

        Seat result = seatService.reserveSeat(1, scheduledTrip);

        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    void reserveSeat_seatNumberNotAvailable_shouldThrowException() {
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), new Bus("101", 5), LocalDateTime.of(2025, 1, 1, 8, 0));
        Seat seat = new Seat(2L, 2, SeatStatus.BLOCKED, scheduledTrip, 1);
        scheduledTrip.setSeats(Set.of(seat));

        assertThrows(SeatNotAvailableException.class, () -> seatService.reserveSeat(2, scheduledTrip));
    }

    @Test
    void reserveSeat_seatNumberNotFound_shouldThrowException() {
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), new Bus("101", 5), LocalDateTime.of(2025, 1, 1, 8, 0));
        Seat seat = new Seat(5L, 5, SeatStatus.FREE, scheduledTrip, 1);
        scheduledTrip.setSeats(Set.of(seat));

        when(seatRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> seatService.reserveSeat(5, scheduledTrip));
    }

    @Test
    void releaseSeat_validSeat_shouldUpdateSeatStatus() {
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN), new Bus("101", 5), LocalDateTime.of(2025, 1, 1, 8, 0));
        Seat seat = new Seat(1L, 1, SeatStatus.RESERVED, scheduledTrip, 1);
        scheduledTrip.setSeats(Set.of(seat));

        when(seatRepository.save(seat)).thenReturn(seat);

        seatService.releaseSeat(seat);

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.FREE);
    }

    @Test
    void getSeatsForScheduledTrip_validScheduledTripId_shouldReturnSeats() {
        long scheduledTripId = 1L;
        Seat seat1 = new Seat(1L, 1, SeatStatus.FREE, null, 1);
        Seat seat2 = new Seat(2L, 2, SeatStatus.RESERVED, null, 1);

        when(seatRepository.findByScheduledTripId(scheduledTripId)).thenReturn(List.of(seat1, seat2));

        List<SeatResponse> result = seatService.getSeatsForScheduledTrip(scheduledTripId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).seatNumber()).isEqualTo(1);
        assertThat(result.get(0).status()).isEqualTo(SeatStatus.FREE);
        assertThat(result.get(1).seatNumber()).isEqualTo(2);
        assertThat(result.get(1).status()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    void getSeatsForScheduledTrip_noSeatsFound_shouldThrowException() {
        long scheduledTripId = 1L;

        when(seatRepository.findByScheduledTripId(scheduledTripId)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> seatService.getSeatsForScheduledTrip(scheduledTripId));
    }
}

package com.example.busticketbooking.trip.seat.service;

import com.example.busticketbooking.common.exception.NotFoundException;
import com.example.busticketbooking.common.exception.SeatNotAvailableException;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        ScheduledTrip scheduledTrip = new ScheduledTrip();
        Seat seat = new Seat(1L, 1, SeatStatus.FREE, scheduledTrip);
        scheduledTrip.setSeats(Set.of(seat));

        when(seatRepository.findById(any())).thenReturn(Optional.of(seat));
        when(seatRepository.save(seat)).thenReturn(seat);

        Seat result = seatService.reserveSeat(1, scheduledTrip);

        assertThat(result.getSeatNumber()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    void reserveSeat_seatNumberNotAvailable_shouldThrowException() {
        ScheduledTrip scheduledTrip = new ScheduledTrip();
        Seat seat = new Seat(2L, 2, SeatStatus.BLOCKED, scheduledTrip);
        scheduledTrip.setSeats(Set.of(seat));

        assertThrows(SeatNotAvailableException.class, () -> seatService.reserveSeat(2, scheduledTrip));
    }

    @Test
    void reserveSeat_seatNumberNotFound_shouldThrowException() {
        ScheduledTrip scheduledTrip = new ScheduledTrip();
        Seat seat = new Seat(5L, 5, SeatStatus.FREE, scheduledTrip);
        scheduledTrip.setSeats(Set.of(seat));

        when(seatRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> seatService.reserveSeat(5, scheduledTrip));
    }
}

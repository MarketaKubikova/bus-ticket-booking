package com.example.busticketbooking.trip.seat.service;

import com.example.busticketbooking.common.exception.NotFoundException;
import com.example.busticketbooking.common.exception.SeatNotAvailableException;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public Seat reserveSeat(int seatNumber, ScheduledTrip scheduledTrip) {
        Seat scheduledTripSeat = scheduledTrip.getAvailableSeatsForReservation().stream()
                .filter(s -> s.getSeatNumber() == seatNumber)
                .findFirst()
                .orElseThrow(() -> new SeatNotAvailableException(seatNumber));

        Seat seat = seatRepository.findById(scheduledTripSeat.getId())
                .orElseThrow(() -> new NotFoundException("Seat not found"));

        seat.setStatus(SeatStatus.RESERVED);

        return seatRepository.save(seat);
    }
}

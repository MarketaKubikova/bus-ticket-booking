package com.example.busticketbooking.trip.seat.service;

import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.SeatNotAvailableException;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
        log.info("Seat {} in {} is reserved", seatNumber, scheduledTrip);

        return seatRepository.save(seat);
    }

    public void releaseSeat(Seat seat) {
        seat.setStatus(SeatStatus.FREE);
        log.info("Seat {} in {} is free to reserve", seat.getSeatNumber(), seat.getScheduledTrip());
        seatRepository.save(seat);
    }
}

package com.example.busticketbooking.reservation.service;

import com.example.busticketbooking.bus.seat.entity.Seat;
import com.example.busticketbooking.bus.seat.repository.SeatRepository;
import com.example.busticketbooking.common.exception.NotFoundException;
import com.example.busticketbooking.common.exception.SeatNotAvailableException;
import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.mapper.ReservationMapper;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final SeatRepository seatRepository;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        ScheduledTrip scheduledTrip = scheduledTripRepository.findById(request.scheduledTripId())
                .orElseThrow(() -> new NotFoundException("ScheduledTrip with ID '" + request.scheduledTripId() + "' not found"));

        Set<Integer> availableSeats = scheduledTrip.getBus().getAvailableSeats().stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toSet());

        if (!availableSeats.contains(request.seatNumber())) {
            throw new SeatNotAvailableException(request.seatNumber());
        }

        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setScheduledTrip(scheduledTrip);
        reservation.setSeatNumber(request.seatNumber());
        reservation.setBookedAt(LocalDateTime.now());

        Reservation result = reservationRepository.save(reservation);

        log.info("Reservation with id '{}' successfully created at '{}'", result.getId(), result.getBookedAt());

        result.getScheduledTrip().getBus().getSeats().stream()
                .filter(seat -> seat.getSeatNumber() == request.seatNumber())
                .findFirst()
                .ifPresentOrElse(seat -> {
                            log.info("Change seat status to unavailable");
                            seat.setAvailable(false);
                            seatRepository.save(seat);
                        },
                        () -> {
                            throw new NotFoundException("Seat with number '" + request.seatNumber() + "' not found");
                        });

        ReservationResponse response = reservationMapper.toResponseDto(result);
        response.setOrigin(result.getScheduledTrip().getRoute().getOrigin().getName());
        response.setDestination(result.getScheduledTrip().getRoute().getDestination().getName());
        response.setDepartureDateTime(result.getScheduledTrip().getDepartureDateTime());

        log.info("Returning reservation response for reservation with id '{}'", result.getId());
        return response;
    }
}

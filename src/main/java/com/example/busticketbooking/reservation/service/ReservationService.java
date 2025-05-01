package com.example.busticketbooking.reservation.service;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.mapper.ReservationMapper;
import com.example.busticketbooking.reservation.repository.ReservationRepository;
import com.example.busticketbooking.shared.exception.BadRequestException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.trip.seat.service.SeatService;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ScheduledTripRepository scheduledTripRepository;
    private final SeatService seatService;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        ScheduledTrip scheduledTrip = scheduledTripRepository.findById(request.scheduledTripId())
                .orElseThrow(() -> new NotFoundException("ScheduledTrip with ID '" + request.scheduledTripId() + "' not found"));

        Seat seat = seatService.reserveSeat(request.seatNumber(), scheduledTrip);

        Reservation reservation = new Reservation();
        reservation.setScheduledTrip(scheduledTrip);
        reservation.setSeat(seat);
        reservation.setBookedAt(LocalDateTime.now());

        AppUser currentUser = getCurrentAuthenticatedUser();
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

        Reservation savedReservation = reservationRepository.save(reservation);

        log.info("Reservation with id '{}' successfully created at '{}'", savedReservation.getId(), savedReservation.getBookedAt());

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

    private AppUser getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

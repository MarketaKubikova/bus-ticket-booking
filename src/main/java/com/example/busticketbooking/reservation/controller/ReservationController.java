package com.example.busticketbooking.reservation.controller;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.user.entity.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>> getUsersReservations(@AuthenticationPrincipal AppUser user) {
        List<ReservationResponse> response = reservationService.getUsersReservations(user);
        return ResponseEntity.ok(response);
    }
}

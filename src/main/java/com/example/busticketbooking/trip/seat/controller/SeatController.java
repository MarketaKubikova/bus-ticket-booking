package com.example.busticketbooking.trip.seat.controller;

import com.example.busticketbooking.trip.seat.dto.SeatResponse;
import com.example.busticketbooking.trip.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/{scheduled-trip-id}")
    public ResponseEntity<List<SeatResponse>> getSeatsForScheduledTrip(@PathVariable("scheduled-trip-id") long scheduledTripId) {
        List<SeatResponse> seats = seatService.getSeatsForScheduledTrip(scheduledTripId);
        return ResponseEntity.ok(seats);
    }
}

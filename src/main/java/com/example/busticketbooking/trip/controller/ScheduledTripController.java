package com.example.busticketbooking.trip.controller;

import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.trip.dto.ScheduledTripRequest;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.service.ScheduledTripService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduled-trips")
@RequiredArgsConstructor
@Validated
public class ScheduledTripController {
    private final ScheduledTripService scheduledTripService;

    @GetMapping("/search")
    public ResponseEntity<List<ScheduledTripResponse>> searchScheduledTrips(@RequestParam("from") @NotNull String origin,
                                                                            @RequestParam("to") @NotNull String destination,
                                                                            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                            @RequestBody @Valid Tariff tariff) {
        List<ScheduledTripResponse> response = scheduledTripService.getScheduledTripsByRouteAndDepartureDate(origin, destination, date, tariff);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createScheduledTrip(@RequestBody @Valid ScheduledTripRequest request) {
        int createdScheduledTrips = scheduledTripService.generateScheduledTripsByRule(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created " + createdScheduledTrips + " scheduled trips");
    }
}

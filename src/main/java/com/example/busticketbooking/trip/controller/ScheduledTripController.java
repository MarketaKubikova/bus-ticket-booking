package com.example.busticketbooking.trip.controller;

import com.example.busticketbooking.trip.dto.ScheduledTripRequest;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.service.ScheduledTripService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/scheduled-trips")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class ScheduledTripController {
    private final ScheduledTripService scheduledTripService;

    @GetMapping
    public ResponseEntity<List<ScheduledTripResponse>> getScheduledTripsByRouteAndDepartureDate(@RequestBody @Valid RouteRequest request,
                                                                                                @RequestParam("from") LocalDate fromDate,
                                                                                                @RequestParam("to") @NotNull LocalDate toDate) {
        List<ScheduledTripResponse> response = scheduledTripService.getScheduledTripsByRouteAndDepartureDate(request, fromDate, toDate);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<List<ScheduledTripResponse>> createScheduledTrip(@RequestBody @Valid ScheduledTripRequest request) {
        List<ScheduledTripResponse> createdScheduledTrip = scheduledTripService.generateScheduledTripsByRule(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdScheduledTrip);
    }
}

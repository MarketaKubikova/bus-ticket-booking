package com.example.busticketbooking.trip.seat.controller;

import com.example.busticketbooking.trip.seat.dto.SeatResponse;
import com.example.busticketbooking.trip.seat.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Seat Management", description = "Operations related to seat management")
public class SeatController {
    private final SeatService seatService;

    @Operation(
            operationId = "GetSeatsForScheduledTrip",
            summary = "Get seats for a scheduled trip",
            description = "Retrieve a list of seats available for a specific scheduled trip.",
            security = @SecurityRequirement(name = ""),
            parameters = @Parameter(
                    name = "scheduled-trip-id",
                    description = "ID of the scheduled trip for which to retrieve seats",
                    required = true,
                    example = "1"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved seats for the scheduled trip",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SeatResponse.class, type = "array")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Scheduled trip not found"
                    )
            }
    )
    @GetMapping("/{scheduled-trip-id}")
    public ResponseEntity<List<SeatResponse>> getSeatsForScheduledTrip(@PathVariable("scheduled-trip-id") long scheduledTripId) {
        List<SeatResponse> seats = seatService.getSeatsForScheduledTrip(scheduledTripId);
        return ResponseEntity.ok(seats);
    }
}

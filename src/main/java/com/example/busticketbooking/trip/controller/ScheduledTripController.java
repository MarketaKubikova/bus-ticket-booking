package com.example.busticketbooking.trip.controller;

import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.trip.dto.ScheduledTripRequest;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.service.ScheduledTripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Scheduled Trip Management", description = "Operations related to scheduled trip management")
public class ScheduledTripController {
    private final ScheduledTripService scheduledTripService;

    @Operation(
            summary = "Search Scheduled Trips",
            operationId = "SearchScheduledTrips",
            description = "Search for scheduled trips based on origin, destination, and departure date.",
            security = @SecurityRequirement(name = ""),
            parameters = {
                    @Parameter(name = "from", description = "Origin location", required = true, schema = @Schema(type = "string", example = "Prague")),
                    @Parameter(name = "to", description = "Destination location", required = true, schema = @Schema(type = "string", example = "Vienna")),
                    @Parameter(name = "date", description = "Departure date in ISO format (yyyy-MM-dd)", schema = @Schema(type = "string", format = "date", example = "2023-10-01"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tariff details for the trip",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Tariff.class, type = "object")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of scheduled trips matching the search criteria",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ScheduledTripResponse.class, type = "array", minProperties = 1)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid date format or missing parameters")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No scheduled trips found for the given criteria",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "No scheduled trips found for the specified route and date")
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<ScheduledTripResponse>> searchScheduledTrips(@RequestParam("from") @NotNull String origin,
                                                                            @RequestParam("to") @NotNull String destination,
                                                                            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                            @RequestBody @Valid Tariff tariff) {
        List<ScheduledTripResponse> response = scheduledTripService.getScheduledTripsByRouteAndDepartureDate(origin, destination, date, tariff);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create Scheduled Trip",
            operationId = "CreateScheduledTrip",
            description = "Create a new scheduled trip based on the provided request.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Scheduled trip request containing details for creating a new scheduled trip",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScheduledTripRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created scheduled trips",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Successfully created 5 scheduled trips")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid scheduled trip request")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have ADMIN role"
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createScheduledTrip(@RequestBody @Valid ScheduledTripRequest request) {
        int createdScheduledTrips = scheduledTripService.generateScheduledTripsByRule(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created " + createdScheduledTrips + " scheduled trips");
    }
}

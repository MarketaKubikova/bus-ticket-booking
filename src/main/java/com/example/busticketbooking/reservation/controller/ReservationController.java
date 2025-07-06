package com.example.busticketbooking.reservation.controller;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.user.entity.AppUser;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Reservation Management", description = "Operations related to reservation management")
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(
            operationId = "CreateReservation",
            summary = "Create a new reservation",
            description = "Create a new reservation for a scheduled trip.",
            security = {
                    @SecurityRequirement(name = "")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Reservation data to book the ticket",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequest.class)
                    )
            )
            ,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created reservation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid reservation request")
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            operationId = "GetUsersReservations",
            summary = "Get all reservations for the authenticated user",
            description = "Retrieve a list of all reservations made by the authenticated user. Only accessible by users with USER role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user's reservations",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class, type = "array")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have USER role"
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>> getUsersReservations(@AuthenticationPrincipal AppUser user) {
        List<ReservationResponse> response = reservationService.getUsersReservations(user);
        return ResponseEntity.ok(response);
    }

    @Operation(
            operationId = "CancelReservation",
            summary = "Cancel a reservation",
            description = "Cancel an existing reservation by its ID. Only accessible by users with USER role.",
            parameters = @Parameter(
                    name = "reservationId",
                    description = "ID of the reservation to cancel",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64", example = "12345")
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully cancelled reservation"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid reservation ID"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have USER role"
                    )
            }
    )
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelReservation(@RequestParam(value = "reservationId") @NotNull Long reservationId,
                                                  @AuthenticationPrincipal AppUser user) {
        reservationService.cancelReservation(reservationId, user);
        return ResponseEntity.noContent().build();
    }
}

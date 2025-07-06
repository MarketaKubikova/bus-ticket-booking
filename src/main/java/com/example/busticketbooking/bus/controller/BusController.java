package com.example.busticketbooking.bus.controller;

import com.example.busticketbooking.bus.dto.BusRequest;
import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Bus Management", description = "Operations related to bus management")
public class BusController {
    private final BusService busService;

    @Operation(
            operationId = "GetAllBuses",
            summary = "Get all buses",
            description = "Retrieve a list of all buses in the system. Only accessible by users with ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of all buses",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BusResponse.class, type = "array")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have ADMIN role"
                    ),

            }
    )
    @GetMapping
    public ResponseEntity<List<BusResponse>> getAllBuses() {
        List<BusResponse> busList = busService.getAllBuses();

        return ResponseEntity.ok(busList);
    }

    @Operation(
            operationId = "GetBusById",
            summary = "Get bus by ID",
            description = "Retrieve details of a specific bus by its ID. Only accessible by users with ADMIN role.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the bus to retrieve",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64", example = "145"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved bus details",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BusResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Bus not found"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have ADMIN role"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BusResponse> getBusById(@PathVariable Long id) {
        BusResponse bus = busService.getBusById(id);

        return ResponseEntity.ok(bus);
    }

    @Operation(
            operationId = "CreateBus",
            summary = "Create a new bus",
            description = "Create a new bus in the system. Only accessible by users with ADMIN role.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bus data to create new bus",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created bus",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BusResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid bus data provided")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have ADMIN role",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "You do not have permission to perform this action")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - Bus with the same number already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Bus with number {busNumber} already exists")
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<BusResponse> createBus(@RequestBody @Valid BusRequest request) {
        BusResponse bus = busService.createBus(request);

        return new ResponseEntity<>(bus, HttpStatus.CREATED);
    }
}

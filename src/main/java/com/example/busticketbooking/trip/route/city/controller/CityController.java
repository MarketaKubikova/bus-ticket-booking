package com.example.busticketbooking.trip.route.city.controller;

import com.example.busticketbooking.trip.route.city.dto.CityRequest;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Tag(name = "City Management", description = "Operations related to city management")
public class CityController {
    private final CityService cityService;

    @Operation(
            operationId = "CreateCity",
            summary = "Create a new city",
            description = "Create a new city in the system. Only accessible by users with ADMIN role.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "City details to be created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CityRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "City created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CityResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid city request data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid city request data")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have ADMIN role",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Access denied")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - City already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "City already exists")
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityResponse> createCity(@RequestBody @Valid CityRequest request) {
        CityResponse response = cityService.createCity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            operationId = "GetAllCities",
            summary = "Get all cities",
            description = "Retrieve a list of all cities in the system.",
            security = @SecurityRequirement(name = ""),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of cities",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CityResponse.class, type = "array")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No cities found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "No cities found")
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<CityResponse>> getAllCities() {
        List<CityResponse> response = cityService.getAllCities();
        return ResponseEntity.ok(response);
    }
}

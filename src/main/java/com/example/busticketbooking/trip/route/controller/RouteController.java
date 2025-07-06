package com.example.busticketbooking.trip.route.controller;

import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Route Management", description = "Operations related to route management")
public class RouteController {
    private final RouteService routeService;

    @Operation(
            operationId = "CreateRoute",
            summary = "Create a new route",
            description = "Allows users with ADMIN role to create a new route.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Route details to be created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RouteRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Route created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RouteResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid route request data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid route request data")
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
                            description = "Conflict - Route already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Route already exists")
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@RequestBody @Valid RouteRequest request) {
        RouteResponse response = routeService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            operationId = "GetAllRoutes",
            summary = "Get all routes",
            description = "Retrieve a list of all routes available in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all routes",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RouteResponse.class, type = "array")
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
                            responseCode = "404",
                            description = "No routes found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "No routes available")
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        List<RouteResponse> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @Operation(
            operationId = "UpdateBasePrice",
            summary = "Update base price of a route",
            description = "Allows users with ADMIN role to update the base price of a specific route.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the route to update",
                            required = true,
                            schema = @Schema(type = "integer", format = "int64", example = "1")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New base price for the route",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "number", format = "decimal", example = "100.00")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Base price updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RouteResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid base price value",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Base price must be greater than 0")
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
                            responseCode = "404",
                            description = "Route not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Route with the specified ID does not exist")
                            )
                    )
            }
    )
    @PatchMapping("/{id}/base-price")
    public ResponseEntity<RouteResponse> updateBasePrice(@PathVariable long id,
                                                         @RequestBody @NotNull @Min(value = 0, message = "Base price must be greater than 0") BigDecimal basePrice) {
        RouteResponse response = routeService.updateBasePrice(id, basePrice);
        return ResponseEntity.ok(response);
    }
}

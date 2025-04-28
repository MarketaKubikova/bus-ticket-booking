package com.example.busticketbooking.trip.route.city.controller;

import com.example.busticketbooking.trip.route.city.dto.CityRequest;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CityController {
    private final CityService cityService;

    @PostMapping
    public ResponseEntity<CityResponse> createCity(@RequestBody @Valid CityRequest request) {
        CityResponse response = cityService.createCity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CityResponse>> getAllCities() {
        List<CityResponse> response = cityService.getAllCities();
        return ResponseEntity.ok(response);
    }
}

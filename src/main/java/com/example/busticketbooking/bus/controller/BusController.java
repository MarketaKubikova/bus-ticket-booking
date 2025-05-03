package com.example.busticketbooking.bus.controller;

import com.example.busticketbooking.bus.dto.BusRequest;
import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.service.BusService;
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
public class BusController {
    private final BusService busService;

    @GetMapping
    public ResponseEntity<List<BusResponse>> getAllBuses() {
        List<BusResponse> busList = busService.getAllBuses();

        return ResponseEntity.ok(busList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponse> getBusById(@PathVariable Long id) {
        BusResponse bus = busService.getBusById(id);

        return ResponseEntity.ok(bus);
    }

    @PostMapping
    public ResponseEntity<BusResponse> createBus(@RequestBody @Valid BusRequest request) {
        BusResponse bus = busService.createBus(request);

        return new ResponseEntity<>(bus, HttpStatus.CREATED);
    }
}

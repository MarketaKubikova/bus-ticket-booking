package com.example.busticketbooking.bus.service;

import com.example.busticketbooking.bus.dto.BusRequest;
import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.bus.mapper.BusMapper;
import com.example.busticketbooking.bus.repository.BusRepository;
import com.example.busticketbooking.common.exception.AlreadyExistsException;
import com.example.busticketbooking.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {
    private final BusRepository busRepository;
    private final BusMapper busMapper;

    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(busMapper::toResponseDto)
                .toList();
    }

    public BusResponse getBusById(Long id) {
        return busRepository.findById(id)
                .map(busMapper::toResponseDto)
                .orElseThrow(() -> new NotFoundException("Bus with ID '" + id + "' not found"));
    }

    public BusResponse createBus(BusRequest request) {
        if (busRepository.existsByBusNumber(request.busNumber())) {
            throw new AlreadyExistsException("Bus with number " + request.busNumber() + " already exists");
        }
        Bus savedBus = busRepository.save(busMapper.toEntity(request));

        return busMapper.toResponseDto(savedBus);
    }
}

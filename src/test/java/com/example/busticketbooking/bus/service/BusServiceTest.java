package com.example.busticketbooking.bus.service;

import com.example.busticketbooking.bus.dto.BusRequest;
import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.bus.mapper.BusMapper;
import com.example.busticketbooking.bus.repository.BusRepository;
import com.example.busticketbooking.common.exception.AlreadyExistsException;
import com.example.busticketbooking.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusMapper busMapper;

    @InjectMocks
    private BusService busService;

    @Test
    void getAllBuses_returnsAllBuses() {
        Bus bus1 = new Bus("99", 5);
        Bus bus2 = new Bus("100", 10);

        when(busRepository.findAll()).thenReturn(List.of(bus1, bus2));
        when(busMapper.toResponseDto(bus1)).thenReturn(new BusResponse("99", 5));
        when(busMapper.toResponseDto(bus2)).thenReturn(new BusResponse("100", 10));

        List<BusResponse> result = busService.getAllBuses();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().busNumber()).isEqualTo("99");
        assertThat(result.getFirst().capacity()).isEqualTo(5);
        assertThat(result.getLast().busNumber()).isEqualTo("100");
        assertThat(result.getLast().capacity()).isEqualTo(10);
    }

    @Test
    void getBusById_busExists_returnsBus() {
        Bus bus = new Bus("99", 5);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busMapper.toResponseDto(bus)).thenReturn(new BusResponse("99", 5));

        BusResponse result = busService.getBusById(1L);

        assertThat(result.busNumber()).isEqualTo("99");
        assertThat(result.capacity()).isEqualTo(5);
    }

    @Test
    void getBusById_busDoesNotExist_throwsNotFoundException() {
        when(busRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> busService.getBusById(1L));
    }

    @Test
    void createBus_busDoesNotExist_createsBus() {
        Bus bus = new Bus("99", 5);
        BusResponse response = new BusResponse("99", 5);

        when(busRepository.existsByBusNumber("99")).thenReturn(false);
        when(busRepository.save(bus)).thenReturn(bus);
        when(busMapper.toEntity(new BusRequest("99", 5))).thenReturn(bus);
        when(busMapper.toResponseDto(bus)).thenReturn(response);

        BusResponse result = busService.createBus(new BusRequest("99", 5));

        assertThat(result.busNumber()).isEqualTo("99");
        assertThat(result.capacity()).isEqualTo(5);
    }

    @Test
    void createBus_busAlreadyExists_throwsAlreadyExistsException() {
        BusRequest request = new BusRequest("99", 5);
        when(busRepository.existsByBusNumber("99")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> busService.createBus(request));
    }
}

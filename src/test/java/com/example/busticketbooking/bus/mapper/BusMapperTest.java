package com.example.busticketbooking.bus.mapper;

import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.entity.Bus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BusMapperTest {

    private BusMapper busMapper;

    @BeforeEach
    void setup() {
        busMapper = Mappers.getMapper(BusMapper.class);
    }


    @Test
    void mapBusToBusResponse_withValidBus_shouldReturnCorrectBusResponse() {
        Bus bus = new Bus("101", 3);
        BusResponse response = busMapper.toResponseDto(bus);

        assertNotNull(response);
        assertEquals("101", response.busNumber());
        assertEquals(3, response.capacity());
    }

    @Test
    void mapBusToBusResponse_withNullBus_shouldReturnNull() {
        BusResponse response = busMapper.toResponseDto(null);

        assertNull(response);
    }

    @Test
    void mapBusToBusResponse_withBusHavingNoSeats_shouldReturnResponseWithZeroSeats() {
        Bus bus = new Bus("102", 0);
        BusResponse response = busMapper.toResponseDto(bus);

        assertNotNull(response);
        assertEquals("102", response.busNumber());
        assertEquals(0, response.capacity());
    }
}

package com.example.busticketbooking.trip.mapper;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ScheduledTripMapperTest {

    private ScheduledTripMapper scheduledTripMapper;

    @BeforeEach
    void setup() {
        scheduledTripMapper = Mappers.getMapper(ScheduledTripMapper.class);
    }

    @Test
    void mapScheduledTripToScheduledTripResponse_withValidScheduledTrip_shouldReturnCorrectScheduledTripResponse() {
        Route route = new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna"), 334.0, Duration.ofHours(4));
        ScheduledTrip scheduledTrip = new ScheduledTrip(route, new Bus("101", 3), LocalDateTime.of(2025, 1, 1, 11, 0, 0));

        ScheduledTripResponse response = scheduledTripMapper.toResponseDto(scheduledTrip);

        assertNotNull(response);
        assertEquals("101", response.busNumber());
        assertEquals("Prague", response.origin());
        assertEquals("Vienna", response.destination());
        assertEquals(LocalDateTime.of(2025, 1, 1, 11, 0, 0), response.departureDateTime());
        assertEquals(LocalDateTime.of(2025, 1, 1, 15, 0, 0), response.arrivalDateTime());
        assertEquals(3, response.seats().size());
    }
}

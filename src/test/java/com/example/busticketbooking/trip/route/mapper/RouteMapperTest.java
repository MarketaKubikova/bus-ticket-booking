package com.example.busticketbooking.trip.route.mapper;

import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.entity.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RouteMapperTest {
    private RouteMapper routeMapper;

    @BeforeEach
    void setup() {
        routeMapper = Mappers.getMapper(RouteMapper.class);
    }

    @Test
    void mapRouteToRouteResponse_withValidRoute_shouldReturnCorrectRouteResponse() {
        Route route = new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna"), 334.0, Duration.ofHours(4), BigDecimal.TEN);

        RouteResponse response = routeMapper.toResponseDto(route);

        assertNotNull(response);
        assertEquals("Prague", response.origin());
        assertEquals("Vienna", response.destination());
        assertEquals(334.0, response.distance());
        assertEquals(Duration.ofHours(4), response.duration());
        assertEquals(BigDecimal.TEN, response.basePriceCzk());
    }

    @Test
    void mapRouteRequestToRoute_withValidRouteRequest_shouldReturnCorrectRoute() {
        RouteRequest request = new RouteRequest("Prague", "Vienna", 334.0, "04:00", BigDecimal.TEN);

        Route route = routeMapper.toEntity(request);

        assertNotNull(route);
        assertNull(route.getOrigin());
        assertNull(route.getDestination());
        assertNull(route.getDuration());
        assertEquals(334.0, route.getDistance());
        assertEquals(BigDecimal.TEN, route.getBasePriceCzk());
    }
}

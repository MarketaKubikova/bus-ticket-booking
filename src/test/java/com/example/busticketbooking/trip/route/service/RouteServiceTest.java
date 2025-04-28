package com.example.busticketbooking.trip.route.service;

import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.mapper.RouteMapper;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private RouteMapper routeMapper;
    @InjectMocks
    private RouteService service;

    @Test
    void createRoute_validRequest_shouldReturnRoute() {
        RouteRequest request = new RouteRequest("Prague", "Berlin", 350.0, Duration.ofHours(4).plusMinutes(30));
        Route route = new Route(null, new City(1L, "Prague"), new City(2L, "Berlin"), 350.0, Duration.ofHours(4).plusMinutes(30));
        Route savedRoute = new Route(1L, new City(1L, "Prague"), new City(2L, "Berlin"), 350.0, Duration.ofHours(4).plusMinutes(30));

        when(routeRepository.existsByOriginNameAndDestinationName("Prague", "Berlin")).thenReturn(false);
        when(routeMapper.toEntity(request)).thenReturn(route);
        when(routeRepository.save(route)).thenReturn(savedRoute);
        when(routeMapper.toResponseDto(savedRoute)).thenReturn(new RouteResponse("Prague", "Berlin", 350.0, Duration.ofHours(4).plusMinutes(30)));

        RouteResponse result = service.createRoute(request);

        assertThat(result).isNotNull();
        assertThat(result.origin()).isEqualTo("Prague");
        assertThat(result.destination()).isEqualTo("Berlin");
        assertThat(result.distance()).isEqualTo(350.0);
        assertThat(result.duration()).isEqualTo(Duration.ofHours(4).plusMinutes(30));
    }
}

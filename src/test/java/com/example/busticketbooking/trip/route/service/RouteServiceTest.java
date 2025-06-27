package com.example.busticketbooking.trip.route.service;

import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.RouteNotFoundException;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.city.repository.CityRepository;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private RouteMapper routeMapper;
    @InjectMocks
    private RouteService service;

    private final Route route = new Route(null, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Berlin", ZoneId.of("Europe/Berlin")), 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.TEN);

    @Test
    void createRoute_validRequest_shouldReturnRouteResponse() {
        RouteRequest request = new RouteRequest("Prague", "Berlin", 350.0, "04:30", BigDecimal.TEN);
        Route savedRoute = new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Berlin", ZoneId.of("Europe/Berlin")), 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.TEN);

        when(routeRepository.existsByOriginNameAndDestinationName("Prague", "Berlin")).thenReturn(false);
        when(cityRepository.findByName("Prague")).thenReturn(Optional.of(new City(1L, "Prague", ZoneId.of("Europe/Prague"))));
        when(cityRepository.findByName("Berlin")).thenReturn(Optional.of(new City(2L, "Berlin", ZoneId.of("Europe/Berlin"))));
        when(routeMapper.toEntity(request)).thenReturn(route);
        when(routeRepository.save(route)).thenReturn(savedRoute);
        when(routeMapper.toResponseDto(savedRoute)).thenReturn(new RouteResponse("Prague", "Berlin", 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.TEN));

        RouteResponse result = service.createRoute(request);

        assertThat(result).isNotNull();
        assertThat(result.origin()).isEqualTo("Prague");
        assertThat(result.destination()).isEqualTo("Berlin");
        assertThat(result.distance()).isEqualTo(350.0);
        assertThat(result.duration()).isEqualTo(Duration.ofHours(4).plusMinutes(30));
        assertThat(result.basePriceCzk()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void getAllRoutes_foundRoutes_shouldReturnRouteList() {
        Route route1 = new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Berlin", ZoneId.of("Europe/Berlin")), 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.TEN);
        Route route2 = new Route(2L, new City(3L, "Vienna", ZoneId.of("Europe/Vienna")), new City(4L, "Budapest", ZoneId.of("Europe/Budapest")), 250.0, Duration.ofHours(3).plusMinutes(15), BigDecimal.valueOf(8.50));
        List<Route> routes = List.of(route1, route2);

        when(routeRepository.findAll()).thenReturn(routes);
        when(routeMapper.toResponseDto(route1)).thenReturn(new RouteResponse("Prague", "Berlin", 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.TEN));
        when(routeMapper.toResponseDto(route2)).thenReturn(new RouteResponse("Vienna", "Budapest", 250.0, Duration.ofHours(3).plusMinutes(15), BigDecimal.valueOf(8.50)));

        List<RouteResponse> result = service.getAllRoutes();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).origin()).isEqualTo("Prague");
        assertThat(result.get(1).origin()).isEqualTo("Vienna");
    }

    @Test
    void getAllRoutes_noRoutesFound_shouldThrowNotFoundException() {
        when(routeRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> service.getAllRoutes());
    }

    @Test
    void updateBasePrice_validRequest_shouldReturnRouteResponse() {
        BigDecimal priceRequest = BigDecimal.valueOf(12.50);
        Route updatedRoute = new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Berlin", ZoneId.of("Europe/Berlin")), 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.valueOf(12.50));
        RouteResponse routeResponse = new RouteResponse("Prague", "Berlin", 350.0, Duration.ofHours(4).plusMinutes(30), BigDecimal.valueOf(12.50));

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenReturn(updatedRoute);
        when(routeMapper.toResponseDto(updatedRoute)).thenReturn(routeResponse);

        RouteResponse result = service.updateBasePrice(1L, priceRequest);

        assertThat(result.origin()).isEqualTo("Prague");
        assertThat(result.destination()).isEqualTo("Berlin");
        assertThat(result.distance()).isEqualTo(350.0);
        assertThat(result.duration()).isEqualTo(Duration.ofHours(4).plusMinutes(30));
        assertThat(result.basePriceCzk()).isEqualTo(BigDecimal.valueOf(12.50));
    }

    @Test
    void updateBasePrice_routeNotFound_shouldThrowException() {
        BigDecimal priceRequest = BigDecimal.valueOf(12.50);

        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> service.updateBasePrice(1L, priceRequest));
    }
}

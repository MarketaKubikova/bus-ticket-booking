package com.example.busticketbooking.trip.route.service;

import com.example.busticketbooking.common.exception.AlreadyExistsException;
import com.example.busticketbooking.common.exception.NotFoundException;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.mapper.RouteMapper;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    public RouteResponse createRoute(RouteRequest request) {
        if (routeRepository.existsByOriginNameAndDestinationName(request.origin(), request.destination())) {
            log.error("Route from {} to {} already exists", request.origin(), request.destination());
            throw new AlreadyExistsException("Route from " + request.origin() + " to " + request.destination() + " already exists");
        }

        Route savedRoute = routeRepository.save(routeMapper.toEntity(request));
        log.info("New route from {} to {} successfully saved", request.origin(), request.destination());

        return routeMapper.toResponseDto(savedRoute);
    }

    public List<RouteResponse> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();

        if (routes.isEmpty()) {
            log.error("No routes found");
            throw new NotFoundException("No routes found");
        }

        return routes.stream()
                .map(routeMapper::toResponseDto)
                .toList();
    }
}

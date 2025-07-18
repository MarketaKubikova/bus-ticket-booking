package com.example.busticketbooking.trip.route.service;

import com.example.busticketbooking.shared.exception.AlreadyExistsException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.RouteNotFoundException;
import com.example.busticketbooking.shared.util.Constant;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.city.repository.CityRepository;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.mapper.RouteMapper;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final RouteRepository routeRepository;
    private final CityRepository cityRepository;
    private final RouteMapper routeMapper;

    public RouteResponse createRoute(RouteRequest request) {
        if (routeRepository.existsByOriginNameAndDestinationName(request.origin(), request.destination())) {
            log.error("Route from {} to {} already exists", request.origin(), request.destination());
            throw new AlreadyExistsException("Route from " + request.origin() + " to " + request.destination() + " already exists");
        }

        City origin = cityRepository.findByName(request.origin())
                .orElseGet(() -> {
                    log.info("City {} not found, creating new city with zone id {}", request.origin(), Constant.ZONE_PRAGUE);
                    return cityRepository.save(new City(request.origin()));
                });

        City destination = cityRepository.findByName(request.destination())
                .orElseGet(() -> {
                    log.info("City {} not found, creating new city with zone id {}", request.origin(), Constant.ZONE_PRAGUE);
                    return cityRepository.save(new City(request.destination()));
                });

        Route route = routeMapper.toEntity(request);
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setDuration(Duration.parse("PT" + request.duration().replace(":", "H") + "M"));
        Route savedRoute = routeRepository.save(route);
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

    public RouteResponse updateBasePrice(long id, BigDecimal basePrice) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new RouteNotFoundException(id));

        route.setBasePriceCzk(basePrice);
        Route updatedRoute = routeRepository.save(route);
        log.info("Base price for route with id {} updated to {}", id, basePrice);

        return routeMapper.toResponseDto(updatedRoute);
    }
}

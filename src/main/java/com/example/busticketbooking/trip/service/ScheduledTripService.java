package com.example.busticketbooking.trip.service;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.bus.repository.BusRepository;
import com.example.busticketbooking.common.exception.NoTripCreatedException;
import com.example.busticketbooking.common.exception.NotFoundException;
import com.example.busticketbooking.common.exception.RouteNotFoundException;
import com.example.busticketbooking.trip.dto.ScheduledTripRequest;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.mapper.ScheduledTripMapper;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTripService {

    private final ScheduledTripRepository scheduledTripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final ScheduledTripMapper scheduledTripMapper;

    public List<ScheduledTripResponse> getScheduledTripsByRouteAndDepartureDate(RouteRequest request, LocalDate fromDate, LocalDate toDate) {
        Route route = getRoute(request.origin(), request.destination());
        if (fromDate == null) {
            log.info("Starting date is not filled, using the current date");
            fromDate = LocalDate.now();
        }

        List<ScheduledTrip> result = scheduledTripRepository.findAllByRouteAndDepartureDateBetween(route, fromDate, toDate);

        if (result.isEmpty()) {
            log.error("No scheduled trips found for the given route and date range: {} to {}", fromDate, toDate);
            throw new NotFoundException("No scheduled trips found for the given route and date range");
        }

        return result.stream()
                .map(scheduledTripMapper::toResponseDto)
                .toList();
    }

    public List<ScheduledTripResponse> generateScheduledTripsByRule(ScheduledTripRequest request) {
        Bus bus = busRepository.findByBusNumber(request.busNumber()).orElseThrow(() -> {
            log.error("Bus not found: {}", request.busNumber());
            return new NotFoundException("Bus " + request.busNumber() + " not found");
        });
        Route route = getRoute(request.origin(), request.destination());

        List<LocalDate> departureDates = request.fromDate().datesUntil(request.toDate().plusDays(1)).toList();

        List<ScheduledTrip> tripsToSave = departureDates.stream()
                .filter(date -> request.departureDay().contains(date.getDayOfWeek()))
                .map(date -> new ScheduledTrip(route, bus, date.atTime(request.departureTime())))
                .toList();

        if (tripsToSave.isEmpty()) {
            log.error("No scheduled trips created for the given date range: {} to {}", request.fromDate(), request.toDate());
            throw new NoTripCreatedException("No scheduled trip was created because the date range is invalid");
        }

        List<ScheduledTrip> savedTrips = scheduledTripRepository.saveAll(tripsToSave);

        return savedTrips.stream()
                .map(scheduledTripMapper::toResponseDto)
                .toList();
    }

    private Route getRoute(String origin, String destination) {
        return routeRepository.findByOriginNameAndDestinationName(origin, destination)
                .orElseThrow(() -> {
                    log.error("Route not found: {} to {}", origin, destination);
                    return new RouteNotFoundException(origin, destination);
                });
    }
}

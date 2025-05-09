package com.example.busticketbooking.trip.service;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.bus.repository.BusRepository;
import com.example.busticketbooking.pricing.service.PricingService;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.shared.exception.NoTripCreatedException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.RouteNotFoundException;
import com.example.busticketbooking.trip.dto.ScheduledTripRequest;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.repository.ScheduledTripRepository;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTripService {

    private final ScheduledTripRepository scheduledTripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final PricingService pricingService;
    private final UserService userService;

    public List<ScheduledTripResponse> getScheduledTripsByRouteAndDepartureDate(String origin, String destination, LocalDate date, Tariff tariff) {
        Route route = getRoute(origin, destination);
        if (date == null) {
            log.info("Date is not filled, using the current date");
            date = LocalDate.now();
        }

        List<ScheduledTrip> foundTrips = scheduledTripRepository.findAllByRouteAndDepartureDate(route, date);

        if (foundTrips.isEmpty()) {
            log.error("No scheduled trips found from {} to {} on {}", origin, destination, date);
            throw new NotFoundException("No scheduled trips found from " + origin + " to " + destination + " on " + date);
        }

        return foundTrips.stream()
                .map(entity -> new ScheduledTripResponse(
                        entity.getBus().getBusNumber(),
                        entity.getRoute().getOrigin().getName(),
                        entity.getRoute().getDestination().getName(),
                        entity.getDepartureDateTime(),
                        entity.getArrivalDateTime(),
                        entity.getAvailableSeatsForReservation().size(),
                        getPriceForTrip(entity, tariff)
                ))
                .toList();
    }

    public int generateScheduledTripsByRule(ScheduledTripRequest request) {
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

        return savedTrips.size();
    }

    private Route getRoute(String origin, String destination) {
        return routeRepository.findByOriginNameAndDestinationName(origin, destination)
                .orElseThrow(() -> {
                    log.error("Route not found: {} to {}", origin, destination);
                    return new RouteNotFoundException(origin, destination);
                });
    }

    private BigDecimal getPriceForTrip(ScheduledTrip scheduledTrip, Tariff tariff) {
        AppUser currentUser = userService.getCurrentAuthenticatedUser();

        return pricingService.calculatePrice(scheduledTrip, currentUser, tariff);
    }
}

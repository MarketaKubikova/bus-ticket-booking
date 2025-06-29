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
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import com.example.busticketbooking.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledTripServiceTest {
    private final Route route = new Route(1L, new City(1L, "Prague", ZoneId.of("Europe/Prague")), new City(2L, "Vienna", ZoneId.of("Europe/Vienna")), 334.0, Duration.ofHours(4), BigDecimal.TEN);
    private final ScheduledTrip scheduledTrip1 = new ScheduledTrip(route, new Bus("101", 5), LocalDateTime.of(2025, 1, 1, 11, 0));
    private final ScheduledTrip scheduledTrip2 = new ScheduledTrip(route, new Bus("102", 3), LocalDateTime.of(2025, 1, 8, 11, 0));
    private final ScheduledTrip scheduledTrip3 = new ScheduledTrip(route, new Bus("101", 5), LocalDateTime.of(2025, 1, 15, 11, 0));
    @Mock
    private ScheduledTripRepository scheduledTripRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private BusRepository busRepository;
    @Mock
    private PricingService pricingService;
    @Mock
    private UserService userService;
    @InjectMocks
    private ScheduledTripService service;

    @Test
    void getScheduledTripsByRouteAndDepartureDate_validData_shouldReturnListOfTrips() {
        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));
        when(scheduledTripRepository.findAllByRouteAndDepartureDate(route, LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of(scheduledTrip1, scheduledTrip2, scheduledTrip3));
        when(pricingService.calculatePrice(scheduledTrip1, null, Tariff.ADULT)).thenReturn(BigDecimal.TEN);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(null);

        List<ScheduledTripResponse> result = service.getScheduledTripsByRouteAndDepartureDate("Prague", "Vienna", LocalDate.of(2025, 1, 1), Tariff.ADULT);

        assertThat(result).hasSize(3);
        assertThat(result.getFirst().origin()).isEqualTo("Prague");
        assertThat(result.getFirst().busNumber()).isEqualTo("101");
        assertThat(result.getFirst().departureDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0), ZoneId.of("Europe/Prague")));
        assertThat(result.getFirst().arrivalDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 15, 0), ZoneId.of("Europe/Vienna")));
        assertThat(result.getFirst().availableSeats()).isEqualTo(5);
        assertThat(result.getFirst().priceCzk()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void getScheduledTripsByRouteAndDepartureDate_missingDate_shouldReturnListOfTrips() {
        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));
        when(scheduledTripRepository.findAllByRouteAndDepartureDate(route, LocalDate.now())).thenReturn(List.of(scheduledTrip1));
        when(pricingService.calculatePrice(scheduledTrip1, null, Tariff.ADULT)).thenReturn(BigDecimal.TEN);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(null);

        List<ScheduledTripResponse> result = service.getScheduledTripsByRouteAndDepartureDate("Prague", "Vienna", null, Tariff.ADULT);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().origin()).isEqualTo("Prague");
        assertThat(result.getFirst().busNumber()).isEqualTo("101");
        assertThat(result.getFirst().departureDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0), ZoneId.of("Europe/Prague")));
        assertThat(result.getFirst().arrivalDateTime()).isEqualTo(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 15, 0), ZoneId.of("Europe/Vienna")));
        assertThat(result.getFirst().availableSeats()).isEqualTo(5);
        assertThat(result.getFirst().priceCzk()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void getScheduledTripsByRouteAndDepartureDate_routeNotFound_shouldThrowException() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> service.getScheduledTripsByRouteAndDepartureDate("Prague", "Vienna", date, Tariff.ADULT));
    }

    @Test
    void generateScheduledTripsByRule_validData_shouldReturnListOfTrips() {
        ScheduledTripRequest request = new ScheduledTripRequest("101", "Prague", "Vienna", LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 14));

        when(busRepository.findByBusNumber("101")).thenReturn(Optional.of(new Bus("101", 5)));
        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));
        when(scheduledTripRepository.saveAll(anyList())).thenReturn(List.of(scheduledTrip1, scheduledTrip2));

        int result = service.generateScheduledTripsByRule(request);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void generateScheduledTripsByRule_invalidData_shouldThrowException() {
        ScheduledTripRequest request = new ScheduledTripRequest("101", "Prague", "Vienna", LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        when(busRepository.findByBusNumber("101")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.generateScheduledTripsByRule(request));
    }

    @Test
    void generateScheduledTripsByRule_noTripsCreated_shouldThrowException() {
        ScheduledTripRequest request = new ScheduledTripRequest("101", "Prague", "Vienna", LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2));

        when(busRepository.findByBusNumber("101")).thenReturn(Optional.of(new Bus("101", 5)));
        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));

        assertThrows(NoTripCreatedException.class, () -> service.generateScheduledTripsByRule(request));
    }
}

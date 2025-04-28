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
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.route.repository.RouteRepository;
import com.example.busticketbooking.trip.seat.dto.SeatResponse;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledTripServiceTest {
    private final Route route = new Route(1L, new City(1L, "Prague"), new City(2L, "Vienna"), 334.0, Duration.ofHours(4));
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
    private ScheduledTripMapper scheduledTripMapper;
    @InjectMocks
    private ScheduledTripService service;

    @Test
    void getScheduledTripsByRouteAndDepartureDate_validData_shouldReturnListOfTrips() {
        RouteRequest request = new RouteRequest("Prague", "Vienna", 334.0, Duration.ofHours(4));

        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));
        when(scheduledTripRepository.findAllByRouteAndDepartureDateBetween(route, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)))
                .thenReturn(List.of(scheduledTrip1, scheduledTrip2, scheduledTrip3));
        when(scheduledTripMapper.toResponseDto(scheduledTrip1)).thenReturn(new ScheduledTripResponse("101", "Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0), LocalDateTime.of(2025, 1, 1, 15, 0), generateSeatResponseSet(5)));
        when(scheduledTripMapper.toResponseDto(scheduledTrip2)).thenReturn(new ScheduledTripResponse("102", "Prague", "Vienna", LocalDateTime.of(2025, 1, 8, 11, 0), LocalDateTime.of(2025, 1, 8, 15, 0), generateSeatResponseSet(3)));
        when(scheduledTripMapper.toResponseDto(scheduledTrip3)).thenReturn(new ScheduledTripResponse("101", "Prague", "Vienna", LocalDateTime.of(2025, 1, 15, 11, 0), LocalDateTime.of(2025, 1, 15, 15, 0), generateSeatResponseSet(5)));

        List<ScheduledTripResponse> result = service.getScheduledTripsByRouteAndDepartureDate(request, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        assertThat(result).hasSize(3);
        assertThat(result.getFirst().origin()).isEqualTo("Prague");
        assertThat(result.getFirst().busNumber()).isEqualTo("101");
        assertThat(result.getFirst().departureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0));
        assertThat(result.getFirst().arrivalDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 15, 0));
        assertThat(result.getFirst().seats()).hasSize(5);
    }

    @Test
    void getScheduledTripsByRouteAndDepartureDate_missingFromDate_shouldReturnListOfTrips() {
        RouteRequest routeRequest = new RouteRequest("Prague", "Vienna", 334.0, Duration.ofHours(4));

        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));
        when(scheduledTripRepository.findAllByRouteAndDepartureDateBetween(route, LocalDate.now(), LocalDate.of(2025, 1, 31))).thenReturn(List.of(scheduledTrip1));
        when(scheduledTripMapper.toResponseDto(scheduledTrip1)).thenReturn(new ScheduledTripResponse("101", "Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0), LocalDateTime.of(2025, 1, 1, 15, 0), generateSeatResponseSet(5)));

        List<ScheduledTripResponse> result = service.getScheduledTripsByRouteAndDepartureDate(routeRequest, null, LocalDate.of(2025, 1, 31));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().origin()).isEqualTo("Prague");
        assertThat(result.getFirst().busNumber()).isEqualTo("101");
        assertThat(result.getFirst().departureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0));
        assertThat(result.getFirst().arrivalDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 15, 0));
        assertThat(result.getFirst().seats()).hasSize(5);
    }

    @Test
    void getScheduledTripsByRouteAndDepartureDate_routeNotFound_shouldThrowException() {
        RouteRequest routeRequest = new RouteRequest("Prague", "Vienna", 334.0, Duration.ofHours(4));

        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> service.getScheduledTripsByRouteAndDepartureDate(routeRequest, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
    }

    @Test
    void generateScheduledTripsByRule_validData_shouldReturnListOfTrips() {
        ScheduledTripRequest request = new ScheduledTripRequest("101", "Prague", "Vienna", LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 14));

        when(busRepository.findByBusNumber("101")).thenReturn(Optional.of(new Bus("101", 5)));
        when(routeRepository.findByOriginNameAndDestinationName("Prague", "Vienna")).thenReturn(Optional.of(route));
        when(scheduledTripRepository.saveAll(anyList())).thenReturn(List.of(scheduledTrip1, scheduledTrip2));
        when(scheduledTripMapper.toResponseDto(scheduledTrip1)).thenReturn(new ScheduledTripResponse("101", "Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0), LocalDateTime.of(2025, 1, 1, 15, 0), generateSeatResponseSet(5)));
        when(scheduledTripMapper.toResponseDto(scheduledTrip2)).thenReturn(new ScheduledTripResponse("101", "Prague", "Vienna", LocalDateTime.of(2025, 1, 8, 11, 0), LocalDateTime.of(2025, 1, 8, 15, 0), generateSeatResponseSet(5)));

        List<ScheduledTripResponse> result = service.generateScheduledTripsByRule(request);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().origin()).isEqualTo("Prague");
        assertThat(result.getFirst().busNumber()).isEqualTo("101");
        assertThat(result.getFirst().departureDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 0));
        assertThat(result.getFirst().arrivalDateTime()).isEqualTo(LocalDateTime.of(2025, 1, 1, 15, 0));
        assertThat(result.getFirst().seats()).hasSize(5);
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

    private Set<SeatResponse> generateSeatResponseSet(int capacity) {
        Set<SeatResponse> seatResponseSet = new HashSet<>();
        for (int i = 1; i <= capacity; i++) {
            seatResponseSet.add(new SeatResponse(i, SeatStatus.FREE));
        }

        return seatResponseSet;
    }
}

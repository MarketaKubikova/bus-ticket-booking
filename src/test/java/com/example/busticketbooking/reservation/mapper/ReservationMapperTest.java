package com.example.busticketbooking.reservation.mapper;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.seat.entity.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReservationMapperTest {

    private ReservationMapper reservationMapper;

    @BeforeEach
    void setup() {
        reservationMapper = Mappers.getMapper(ReservationMapper.class);
    }

    private static Reservation createReservation() {
        ScheduledTrip scheduledTrip = new ScheduledTrip(new Route(1L, new City(1L, "Vienna", ZoneId.of("Europe/Vienna")), new City(2L, "Budapest", ZoneId.of("Europe/Budapest")), 244.0, Duration.ofHours(3), BigDecimal.TEN), new Bus("101", 3), LocalDateTime.of(2025, 1, 1, 9, 0));
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setSeat(new Seat(1, scheduledTrip));
        reservation.setPassengerEmail("test@test.com");
        reservation.setScheduledTrip(scheduledTrip);
        return reservation;
    }

    @Test
    void toResponseDtoMapsReservationToResponseDto() {
        Reservation reservation = createReservation();

        ReservationResponse responseDto = reservationMapper.toResponseDto(reservation);

        assertNotNull(responseDto);
        assertEquals(1, responseDto.getSeatNumber());
        assertEquals("test@test.com", responseDto.getPassengerEmail());
        assertEquals("Vienna", responseDto.getOrigin());
        assertEquals("Budapest", responseDto.getDestination());
        assertEquals(ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 9, 0), ZoneId.of("Europe/Vienna")), responseDto.getDepartureDateTime());
    }
}

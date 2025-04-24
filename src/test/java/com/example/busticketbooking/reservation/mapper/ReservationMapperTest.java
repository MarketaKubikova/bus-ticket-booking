package com.example.busticketbooking.reservation.mapper;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ReservationMapperTest {

    private ReservationMapper reservationMapper;

    @BeforeEach
    void setup() {
        reservationMapper = Mappers.getMapper(ReservationMapper.class);
    }

    @Test
    void toResponseDtoMapsReservationToResponseDto() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setSeatNumber(1);
        reservation.setPassengerEmail("test@test.com");

        ReservationResponse responseDto = reservationMapper.toResponseDto(reservation);

        assertNotNull(responseDto);
        assertEquals(1, responseDto.getSeatNumber());
        assertEquals("test@test.com", responseDto.getPassengerEmail());
        assertNull(responseDto.getOrigin());
        assertNull(responseDto.getDestination());
        assertNull(responseDto.getDepartureDateTime());
    }

    @Test
    void toEntityMapsRequestDtoToReservation() {
        ReservationRequest requestDto = new ReservationRequest(1L, 1, "test@test.com");

        Reservation reservation = reservationMapper.toEntity(requestDto);

        assertNotNull(reservation);
        assertEquals("test@test.com", reservation.getPassengerEmail());
        assertEquals(0, reservation.getSeatNumber());
        assertNull(reservation.getScheduledTrip());
        assertNull(reservation.getBookedAt());
    }
}

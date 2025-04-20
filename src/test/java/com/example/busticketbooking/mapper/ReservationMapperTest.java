package com.example.busticketbooking.mapper;

import com.example.busticketbooking.model.dto.ReservationRequestDto;
import com.example.busticketbooking.model.dto.ReservationResponseDto;
import com.example.busticketbooking.model.entity.Reservation;
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

        ReservationResponseDto responseDto = reservationMapper.toResponseDto(reservation);

        assertNotNull(responseDto);
        assertEquals(1, responseDto.getSeatNumber());
        assertEquals("test@test.com", responseDto.getPassengerEmail());
        assertNull(responseDto.getOrigin());
        assertNull(responseDto.getDestination());
        assertNull(responseDto.getDepartureDateTime());
    }

    @Test
    void toEntityMapsRequestDtoToReservation() {
        ReservationRequestDto requestDto = new ReservationRequestDto(1L, 1, "test@test.com");

        Reservation reservation = reservationMapper.toEntity(requestDto);

        assertNotNull(reservation);
        assertEquals("test@test.com", reservation.getPassengerEmail());
        assertEquals(0, reservation.getSeatNumber());
        assertNull(reservation.getScheduledTrip());
        assertNull(reservation.getBookedAt());
    }
}

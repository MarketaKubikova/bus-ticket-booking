package com.example.busticketbooking.reservation.mapper;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "departureDateTime", ignore = true)
    ReservationResponse toResponseDto(Reservation reservation);

    @Mapping(target = "scheduledTrip", ignore = true)
    @Mapping(target = "seatNumber", ignore = true)
    Reservation toEntity(ReservationRequest reservationRequest);
}

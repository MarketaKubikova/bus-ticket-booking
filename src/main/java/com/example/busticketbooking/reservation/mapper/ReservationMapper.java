package com.example.busticketbooking.reservation.mapper;

import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "origin", source = "entity.scheduledTrip.route.origin.name")
    @Mapping(target = "destination", source = "entity.scheduledTrip.route.destination.name")
    @Mapping(target = "departureDateTime", source = "entity.scheduledTrip.departureDateTime")
    @Mapping(target = "seatNumber", source = "entity.seat.seatNumber")
    @Mapping(target = "status", source = "entity.status")
    @Mapping(target = "priceCzk", source = "entity.priceCzk")
    ReservationResponse toResponseDto(Reservation entity);
}

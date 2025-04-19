package com.example.busticketbooking.mapper;

import com.example.busticketbooking.model.dto.ReservationRequestDto;
import com.example.busticketbooking.model.dto.ReservationResponseDto;
import com.example.busticketbooking.model.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "departureDateTime", ignore = true)
    ReservationResponseDto toResponseDto(Reservation reservation);

    @Mapping(target = "scheduledTrip", ignore = true)
    @Mapping(target = "seatNumber", ignore = true)
    Reservation toEntity(ReservationRequestDto reservationRequestDto);
}

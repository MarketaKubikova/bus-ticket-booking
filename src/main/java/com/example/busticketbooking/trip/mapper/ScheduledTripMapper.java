package com.example.busticketbooking.trip.mapper;

import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduledTripMapper {
    @Mapping(target = "busNumber", source = "entity.bus.busNumber")
    @Mapping(target = "origin", source = "route.origin.name")
    @Mapping(target = "destination", source = "route.destination.name")
    ScheduledTripResponse toResponseDto(ScheduledTrip entity);
}

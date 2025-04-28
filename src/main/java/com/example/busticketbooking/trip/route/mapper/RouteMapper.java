package com.example.busticketbooking.trip.route.mapper;

import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    @Mapping(target = "origin.name", source = "dto.origin")
    @Mapping(target = "destination.name", source = "dto.destination")
    Route toEntity(RouteRequest dto);

    @Mapping(target = "origin", source = "entity.origin.name")
    @Mapping(target = "destination", source = "entity.destination.name")
    RouteResponse toResponseDto(Route entity);
}

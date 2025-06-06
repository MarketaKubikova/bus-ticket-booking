package com.example.busticketbooking.trip.route.mapper;

import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "basePriceCzk", source = "dto.basePriceCzk")
    Route toEntity(RouteRequest dto);

    @Mapping(target = "origin", source = "entity.origin.name")
    @Mapping(target = "destination", source = "entity.destination.name")
    @Mapping(target = "basePriceCzk", source = "entity.basePriceCzk")
    RouteResponse toResponseDto(Route entity);
}

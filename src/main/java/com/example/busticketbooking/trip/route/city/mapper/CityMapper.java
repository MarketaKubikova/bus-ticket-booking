package com.example.busticketbooking.trip.route.city.mapper;

import com.example.busticketbooking.trip.route.city.dto.CityRequest;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.entity.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {
    City toEntity(CityRequest dto);
    CityResponse toResponseDto(City entity);
}

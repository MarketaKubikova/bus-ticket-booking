package com.example.busticketbooking.trip.route.city.mapper;

import com.example.busticketbooking.trip.route.city.dto.CityRequest;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.entity.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CityMapperTest {
    private CityMapper cityMapper;

    @BeforeEach
    void setup() {
        cityMapper = Mappers.getMapper(CityMapper.class);
    }

    @Test
    void toEntity_mapsRequestDtoToEntity() {
        CityRequest requestDto = new CityRequest("New York", ZoneId.of("America/New_York"));

        City city = cityMapper.toEntity(requestDto);

        assertNotNull(city);
        assertEquals("New York", city.getName());
        assertEquals(ZoneId.of("America/New_York"), city.getZoneId());
    }

    @Test
    void toResponseDto_mapsEntityToResponseDto() {
        City city = new City(1L, "New York", ZoneId.of("America/New_York"));

        CityResponse responseDto = cityMapper.toResponseDto(city);

        assertNotNull(responseDto);
        assertEquals("New York", responseDto.name());
    }
}

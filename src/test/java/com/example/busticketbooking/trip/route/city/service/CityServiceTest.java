package com.example.busticketbooking.trip.route.city.service;

import com.example.busticketbooking.shared.exception.AlreadyExistsException;
import com.example.busticketbooking.trip.route.city.dto.CityRequest;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.city.mapper.CityMapper;
import com.example.busticketbooking.trip.route.city.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock
    private CityRepository cityRepository;
    @Mock
    private CityMapper cityMapper;
    @InjectMocks
    private CityService service;

    @Test
    void createCity_validRequest_shouldReturnCity() {
        CityRequest request = new CityRequest("New York", ZoneId.of("America/New_York"));
        City city = new City(null, "New York", ZoneId.of("America/New_York"));
        City savedCity = new City(1L, "New York", ZoneId.of("America/New_York"));

        when(cityRepository.existsByName("New York")).thenReturn(false);
        when(cityMapper.toEntity(request)).thenReturn(city);
        when(cityRepository.save(city)).thenReturn(savedCity);
        when(cityMapper.toResponseDto(savedCity)).thenReturn(new CityResponse("New York"));

        CityResponse result = service.createCity(request);

        assertThat(result.name()).isEqualTo("New York");
    }

    @Test
    void createCity_cityAlreadyExists_shouldThrowException() {
        CityRequest request = new CityRequest("New York", ZoneId.of("Europe/Prague"));

        when(cityRepository.existsByName("New York")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> service.createCity(request));
    }

    @Test
    void createCity_noZoneId_shouldUseDefaultZoneId() {
        CityRequest request = new CityRequest("Prague", null);
        City city = new City("Prague");

        when(cityRepository.existsByName("Prague")).thenReturn(false);
        when(cityMapper.toEntity(request)).thenReturn(city);
        when(cityRepository.save(city)).thenReturn(city);
        when(cityMapper.toResponseDto(city)).thenReturn(new CityResponse("Prague"));

        CityResponse result = service.createCity(request);

        assertThat(result.name()).isEqualTo("Prague");
        assertThat(city.getZoneId()).isEqualTo(ZoneId.of("Europe/Prague"));
    }
}

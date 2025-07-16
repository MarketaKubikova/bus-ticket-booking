package com.example.busticketbooking.trip.route.city.service;

import com.example.busticketbooking.shared.exception.AlreadyExistsException;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.trip.route.city.dto.CityRequest;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.entity.City;
import com.example.busticketbooking.trip.route.city.mapper.CityMapper;
import com.example.busticketbooking.trip.route.city.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityResponse createCity(CityRequest request) {
        if (cityRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("City " + request.getName() + " already exists");
        }

        City savedCity = cityRepository.save(cityMapper.toEntity(request));

        return cityMapper.toResponseDto(savedCity);
    }

    public List<CityResponse> getAllCities() {
        List<City> cities = cityRepository.findAll();

        if (cities.isEmpty()) {
            log.error("No cities found");
            throw new NotFoundException("No city found");
        }

        log.info("Found {} cities", cities.size());
        return cities.stream()
                .map(cityMapper::toResponseDto)
                .toList();
    }
}

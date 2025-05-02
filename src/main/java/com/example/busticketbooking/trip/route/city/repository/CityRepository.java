package com.example.busticketbooking.trip.route.city.repository;

import com.example.busticketbooking.trip.route.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByName(String name);

    Optional<City> findByName(String name);
}

package com.example.busticketbooking.trip.route.repository;

import com.example.busticketbooking.trip.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByOriginNameAndDestinationName(String origin, String destination);
}

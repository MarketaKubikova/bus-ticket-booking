package com.example.busticketbooking.trip.route.repository;

import com.example.busticketbooking.trip.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByOriginNameAndDestinationName(String origin, String destination);

    @Query("SELECT r FROM Route r WHERE r.origin.name = :origin AND r.destination.name = :destination")
    Optional<Route> findByOriginNameAndDestinationName(String origin, String destination);
}

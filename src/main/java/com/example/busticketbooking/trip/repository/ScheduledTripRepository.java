package com.example.busticketbooking.trip.repository;

import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduledTripRepository extends JpaRepository<ScheduledTrip, Long> {

    @Query(value = """
            SELECT st FROM ScheduledTrip st
            JOIN st.route r
            WHERE r.origin.name = :originName
            AND r.destination.name = :destinationName
            AND st.departureDateTime BETWEEN :fromDate AND :toDate
            ORDER BY st.departureDateTime ASC
            """)
    List<ScheduledTrip> findAllByRouteAndDepartureDateBetween(Route route, LocalDate fromDate, LocalDate toDate);
}

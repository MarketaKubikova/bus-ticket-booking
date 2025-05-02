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

    @Query("SELECT st FROM ScheduledTrip st WHERE st.route = :route AND DATE(st.departureDateTime) = :date ORDER BY st.departureDateTime ASC")
    List<ScheduledTrip> findAllByRouteAndDepartureDate(Route route, LocalDate date);
}

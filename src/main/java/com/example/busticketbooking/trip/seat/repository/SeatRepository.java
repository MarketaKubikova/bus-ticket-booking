package com.example.busticketbooking.trip.seat.repository;

import com.example.busticketbooking.trip.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScheduledTripId(Long scheduledTripId);
}

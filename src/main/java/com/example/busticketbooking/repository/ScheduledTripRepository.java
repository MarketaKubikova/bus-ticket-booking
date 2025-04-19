package com.example.busticketbooking.repository;

import com.example.busticketbooking.model.entity.ScheduledTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTripRepository extends JpaRepository<ScheduledTrip, Long> {
}

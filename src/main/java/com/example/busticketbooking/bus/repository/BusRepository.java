package com.example.busticketbooking.bus.repository;

import com.example.busticketbooking.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByBusNumber(String busNumber);

    Optional<Bus> findByBusNumber(String busNumber);
}

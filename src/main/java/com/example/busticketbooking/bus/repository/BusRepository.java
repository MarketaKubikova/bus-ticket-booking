package com.example.busticketbooking.bus.repository;

import com.example.busticketbooking.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByBusNumber(String busNumber);
}

package com.example.busticketbooking.trip.domain.entity;

import com.example.busticketbooking.route.domain.entity.Route;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "departure_time")
    private LocalTime departureTime;
    @Column(name = "arrival_time")
    private LocalTime arrivalTime;
    @Column(name = "bus_id")
    private Integer busId;
    @ManyToOne(fetch = FetchType.EAGER)
    private Route route;
    private Double price;
    @Column(name = "available_seats")
    private Integer availableSeats;
}

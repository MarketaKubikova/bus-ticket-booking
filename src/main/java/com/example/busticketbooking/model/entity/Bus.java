package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "buses")
@Getter
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bus_number", unique = true, nullable = false)
    private String busNumber;
    private int capacity;
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats = new HashSet<>();

    protected Bus() {
    }

    public Bus(String busNumber, int capacity) {
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.seats = new HashSet<>();

        for (int i = 1; i <= capacity; i++) {
            seats.add(new Seat(i, this));
        }
    }

    public Set<Seat> getAvailableSeats() {
        return seats.stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toSet());
    }
}

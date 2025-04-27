package com.example.busticketbooking.bus.entity;

import jakarta.persistence.*;
import lombok.Getter;

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

    protected Bus() {
    }

    public Bus(String busNumber, int capacity) {
        this.busNumber = busNumber;
        this.capacity = capacity;
    }
}

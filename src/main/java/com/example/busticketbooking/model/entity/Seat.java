package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "seat_number", nullable = false)
    private int seatNumber;
    @ManyToOne
    private Bus bus;
    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    protected Seat() {
    }

    public Seat(int seatNumber, Bus bus) {
        this.seatNumber = seatNumber;
        this.bus = bus;
    }
}

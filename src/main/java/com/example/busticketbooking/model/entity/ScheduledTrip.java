package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_trips")
@Getter
public class ScheduledTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Route route;
    @ManyToOne
    private Bus bus;
    @Column(name = "departure_date_time")
    private LocalDateTime departureDateTime;
}

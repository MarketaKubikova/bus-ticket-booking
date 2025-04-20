package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_trips")
@AllArgsConstructor
@NoArgsConstructor
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

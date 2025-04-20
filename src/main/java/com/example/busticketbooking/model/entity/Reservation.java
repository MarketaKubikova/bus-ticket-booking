package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "scheduled_trip_id")
    private ScheduledTrip scheduledTrip;
    @Column(name = "passenger_email")
    private String passengerEmail;
    @Column(name = "seat_number")
    private int seatNumber;
    @Column(name = "booked_at")
    private LocalDateTime bookedAt;
}

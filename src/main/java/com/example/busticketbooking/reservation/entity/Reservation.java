package com.example.busticketbooking.reservation.entity;

import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.user.entity.AppUser;
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Seat seat;
    @Column(name = "booked_at")
    private LocalDateTime bookedAt;
    @ManyToOne
    private AppUser user;
}

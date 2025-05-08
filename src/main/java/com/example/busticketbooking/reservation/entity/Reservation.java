package com.example.busticketbooking.reservation.entity;

import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.entity.Seat;
import com.example.busticketbooking.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.ACTIVE;
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt = null;
    @Column(name = "price_czk")
    private BigDecimal priceCzk;
    @Enumerated(EnumType.STRING)
    private Tariff tariff;

    public Reservation(Long id, ScheduledTrip scheduledTrip, String passengerEmail, Seat seat, AppUser user, BigDecimal priceCzk) {
        this.id = id;
        this.scheduledTrip = scheduledTrip;
        this.passengerEmail = passengerEmail;
        this.seat = seat;
        this.bookedAt = LocalDateTime.now();
        this.user = user;
        this.priceCzk = priceCzk;
    }
}

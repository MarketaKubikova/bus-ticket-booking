package com.example.busticketbooking.reservation.entity;

import com.example.busticketbooking.payment.entity.PaymentTransaction;
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
import java.time.Instant;

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
    @Column(name = "created_at")
    private Instant createdAt;
    @ManyToOne
    private AppUser user;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.RESERVED;
    @Column(name = "canceled_at")
    private Instant canceledAt = null;
    @Column(name = "price_czk")
    private BigDecimal priceCzk;
    @Enumerated(EnumType.STRING)
    private Tariff tariff;
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private PaymentTransaction paymentTransaction;

    public Reservation(Long id, ScheduledTrip scheduledTrip, String passengerEmail, Seat seat, Instant createdAt, AppUser user, BigDecimal priceCzk) {
        this.id = id;
        this.scheduledTrip = scheduledTrip;
        this.passengerEmail = passengerEmail;
        this.seat = seat;
        this.createdAt = createdAt;
        this.user = user;
        this.priceCzk = priceCzk;
    }
}

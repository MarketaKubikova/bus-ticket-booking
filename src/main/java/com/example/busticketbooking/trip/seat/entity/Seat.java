package com.example.busticketbooking.trip.seat.entity;

import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "seat_number", nullable = false)
    private int seatNumber;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.FREE;
    @ManyToOne
    @JoinColumn(name = "scheduled_trip_id", nullable = false)
    private ScheduledTrip scheduledTrip;

    public Seat(int seatNumber, ScheduledTrip scheduledTrip) {
        this.seatNumber = seatNumber;
        this.scheduledTrip = scheduledTrip;
    }

    public boolean isSeatAvailableForReservation() {
        return this.status.equals(SeatStatus.FREE);
    }
}

package com.example.busticketbooking.trip.entity;

import com.example.busticketbooking.bus.entity.Bus;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.trip.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "scheduled_trips")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "arrival_date_time")
    private LocalDateTime arrivalDateTime;
    @OneToMany(mappedBy = "scheduledTrip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Seat> seats = new HashSet<>();

    public ScheduledTrip(Route route, Bus bus, LocalDateTime departureDateTime) {
        this.route = route;
        this.bus = bus;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = departureDateTime.plus(route.getDuration());

        for (int i = 1; i <= bus.getCapacity(); i++) {
            this.seats.add(new Seat(i, this));
        }
    }

    public Set<Seat> getAvailableSeatsForReservation() {
        return seats.stream()
                .filter(Seat::isSeatAvailableForReservation)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "scheduled trip from " + route.getOrigin().getName() + " to " + route.getDestination().getName() +
                " on " + departureDateTime;
    }
}

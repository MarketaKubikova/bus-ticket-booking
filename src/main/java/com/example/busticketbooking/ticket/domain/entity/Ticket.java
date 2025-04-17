package com.example.busticketbooking.ticket.domain.entity;

import com.example.busticketbooking.passenger.domain.entity.Passenger;
import com.example.busticketbooking.ticket.domain.model.SeatClass;
import com.example.busticketbooking.ticket.domain.model.Tariff;
import com.example.busticketbooking.ticket.domain.model.TicketStatus;
import com.example.busticketbooking.trip.domain.entity.Trip;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER)
    private Passenger passenger;
    @OneToOne(fetch = FetchType.EAGER)
    private Trip trip;
    @Column(name = "seat_number")
    private Integer seatNumber;
    @Column(name = "price")
    private Double price;
    private Tariff tariff;
    @Column(name = "class")
    private SeatClass seatClass;
    private TicketStatus status;
}

package com.example.busticketbooking.passenger.domain.entity;

import com.example.busticketbooking.passenger.domain.model.LoyaltyStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "first_name")
    private String firstName;
    private String surname;
    private String email;
    private String password;
    @Column(name = "collected_kilometers")
    private Long collectedKilometers;
    @Column(name = "loyalty_status")
    private LoyaltyStatus loyaltyStatus;
}

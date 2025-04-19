package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "routes")
@Getter
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private City origin;
    @ManyToOne
    private City destination;
}

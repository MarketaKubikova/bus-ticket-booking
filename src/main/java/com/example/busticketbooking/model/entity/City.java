package com.example.busticketbooking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "cities")
@Getter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}

package com.example.busticketbooking.trip.route.city.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public City(String name) {
        this.name = name;
    }
}

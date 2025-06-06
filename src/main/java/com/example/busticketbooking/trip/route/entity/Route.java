package com.example.busticketbooking.trip.route.entity;

import com.example.busticketbooking.trip.route.city.entity.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "routes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private City origin;
    @ManyToOne
    private City destination;
    @Column(name = "distance_km")
    private Double distance;
    @Column(name = "duration")
    private Duration duration;
    @Column(name = "base_price_czk", nullable = false)
    private BigDecimal basePriceCzk;
}

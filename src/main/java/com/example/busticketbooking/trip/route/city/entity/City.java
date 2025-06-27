package com.example.busticketbooking.trip.route.city.entity;

import com.example.busticketbooking.shared.util.Constant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;

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
    @Column(name = "zone_id")
    private ZoneId zoneId = Constant.ZONE_PRAGUE;

    public City(String name) {
        this.name = name;
    }

    public City(String name, ZoneId zoneId) {
        this.name = name;
        this.zoneId = zoneId;
    }
}

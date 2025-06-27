package com.example.busticketbooking.trip.route.city.dto;

import com.example.busticketbooking.shared.util.Constant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CityRequest {
    @NotNull
    @NotBlank
    private String name;
    private ZoneId zoneId = Constant.ZONE_PRAGUE;
}

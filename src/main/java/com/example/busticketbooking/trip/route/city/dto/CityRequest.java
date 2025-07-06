package com.example.busticketbooking.trip.route.city.dto;

import com.example.busticketbooking.shared.util.Constant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(
        description = "Request object for creating or updating a city",
        requiredProperties = {"name"})
public class CityRequest {
    @NotNull
    @NotBlank
    @Schema(
            description = "Name of the city",
            example = "Prague"
    )
    private String name;
    @Schema(
            description = "Time zone of the city",
            defaultValue = "Europe/Prague",
            example = "Europe/Prague"
    )
    private ZoneId zoneId = Constant.ZONE_PRAGUE;
}

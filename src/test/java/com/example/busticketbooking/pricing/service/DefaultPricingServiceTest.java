package com.example.busticketbooking.pricing.service;

import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.user.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultPricingServiceTest {

    private PricingService pricingService;

    private ScheduledTrip trip;
    private AppUser user;

    @BeforeEach
    void setup() {
        pricingService = new DefaultPricingService(List.of());

        Route route = new Route();
        route.setBasePriceCzk(BigDecimal.valueOf(100));

        trip = new ScheduledTrip();
        trip.setRoute(route);

        user = new AppUser();
    }

    @Test
    void calculatePrice_withStrategies() {
        BigDecimal result = pricingService.calculatePrice(trip, user);

        assertThat(result).isEqualByComparingTo("100.00");
    }
}


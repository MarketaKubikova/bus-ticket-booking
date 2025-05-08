package com.example.busticketbooking.pricing.service;

import com.example.busticketbooking.pricing.context.PricingContext;
import com.example.busticketbooking.pricing.strategy.PricingStrategy;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.trip.route.entity.Route;
import com.example.busticketbooking.user.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPricingServiceTest {
    @Mock
    private PricingStrategy tariffPricingStrategy;

    private PricingService pricingService;

    private ScheduledTrip trip;
    private AppUser user;

    @BeforeEach
    void setup() {
        pricingService = new DefaultPricingService(List.of(tariffPricingStrategy));

        Route route = new Route();
        route.setBasePriceCzk(BigDecimal.valueOf(100));

        trip = new ScheduledTrip();
        trip.setRoute(route);

        user = new AppUser();
    }

    @Test
    void calculatePrice_withStrategies() {
        Tariff tariff = Tariff.CHILD;

        when(tariffPricingStrategy.apply(eq(BigDecimal.valueOf(100)), argThat(ctx ->
                ctx.scheduledTrip() == trip &&
                        ctx.user() == user &&
                        ctx.tariff() == tariff))).thenReturn(BigDecimal.valueOf(50));

        BigDecimal result = pricingService.calculatePrice(trip, user, Tariff.CHILD);

        assertThat(result).isEqualByComparingTo("50.00");
        verify(tariffPricingStrategy).apply(eq(BigDecimal.valueOf(100)), any(PricingContext.class));
    }
}


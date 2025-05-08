package com.example.busticketbooking.pricing.service;

import com.example.busticketbooking.pricing.context.PricingContext;
import com.example.busticketbooking.pricing.strategy.PricingStrategy;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.user.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultPricingService implements PricingService {
    private final List<PricingStrategy> strategies;

    @Override
    public BigDecimal calculatePrice(ScheduledTrip trip, AppUser user, Tariff tariff) {
        BigDecimal price = trip.getRoute().getBasePriceCzk();
        PricingContext context = new PricingContext(trip, user, tariff);

        for (PricingStrategy strategy : strategies) {
            price = strategy.apply(price, context);
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }
}

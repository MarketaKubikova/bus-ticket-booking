package com.example.busticketbooking.pricing.service;

import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.user.entity.AppUser;

import java.math.BigDecimal;

public interface PricingService {
    BigDecimal calculatePrice(ScheduledTrip trip, AppUser user, Tariff tariff);
}

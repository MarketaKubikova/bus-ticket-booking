package com.example.busticketbooking.pricing.context;

import com.example.busticketbooking.trip.entity.ScheduledTrip;
import com.example.busticketbooking.user.entity.AppUser;

public record PricingContext(ScheduledTrip scheduledTrip, AppUser user) {
}

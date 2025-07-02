package com.example.busticketbooking.notification.strategy;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.reservation.entity.Reservation;

import java.util.Set;

public interface NotificationStrategy {
    Set<NotificationType> getSupportedTypes();

    void notify(Reservation reservation, NotificationType type);
}

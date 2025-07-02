package com.example.busticketbooking.notification.service;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.reservation.entity.Reservation;

public interface NotificationService {
    void notify(NotificationType type, Reservation reservation);
}

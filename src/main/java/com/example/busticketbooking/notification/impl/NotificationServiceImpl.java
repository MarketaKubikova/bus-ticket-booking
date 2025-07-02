package com.example.busticketbooking.notification.impl;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.notification.service.NotificationService;
import com.example.busticketbooking.notification.strategy.NotificationStrategy;
import com.example.busticketbooking.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final List<NotificationStrategy> strategies;

    @Override
    public void notify(NotificationType type, Reservation reservation) {
        strategies.stream()
                .filter(strategy -> strategy.getSupportedTypes().contains(type))
                .forEach(strategy -> strategy.notify(reservation, type));
    }
}


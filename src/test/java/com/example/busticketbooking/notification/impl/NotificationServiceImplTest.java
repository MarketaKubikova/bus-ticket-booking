package com.example.busticketbooking.notification.impl;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.notification.strategy.EmailNotificationStrategy;
import com.example.busticketbooking.reservation.entity.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock
    private EmailNotificationStrategy emailNotificationStrategy;

    private NotificationServiceImpl service;

    @BeforeEach
    void setup() {
        service = new NotificationServiceImpl(List.of(emailNotificationStrategy));
    }

    @Test
    void notify_supportedType_shouldCallStrategy() {
        NotificationType type = NotificationType.RESERVATION_CONFIRMED;
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setPassengerEmail("test@test.com");

        when(emailNotificationStrategy.getSupportedTypes()).thenReturn(Set.of(NotificationType.RESERVATION_CONFIRMED));

        service.notify(type, reservation);

        verify(emailNotificationStrategy, times(1)).notify(reservation, type);
    }
}

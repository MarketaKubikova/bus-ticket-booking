package com.example.busticketbooking.notification.strategy;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationStrategy implements NotificationStrategy {

    private final JavaMailSender mailSender;
    @Value("${notification.email.from}")
    private String from;

    @Override
    public Set<NotificationType> getSupportedTypes() {
        return Set.of(NotificationType.RESERVATION_CONFIRMED);
    }

    @Override
    public void notify(Reservation reservation, NotificationType type) {
        if (reservation.getPassengerEmail() == null) {
            log.error("Email address is not provided for reservation ID: {}", reservation.getId());
            throw new IllegalArgumentException("Email address is not provided for reservation ID: " + reservation.getId());
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(reservation.getPassengerEmail());
        mail.setSubject(type.getSubject());
        mail.setText(type.getBodyTemplate());

        log.info("Sending email '{}' for reservation ID: {}", type.name(), reservation.getId());
        mailSender.send(mail);
    }
}


package com.example.busticketbooking.notification.strategy;

import com.example.busticketbooking.notification.model.NotificationType;
import com.example.busticketbooking.reservation.entity.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationStrategyTest {

    @Mock
    private JavaMailSender mailSender;
    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;
    @InjectMocks
    private EmailNotificationStrategy strategy;

    @Test
    void notify_validEmail_shouldSendEmail() {
        Reservation reservation = new Reservation();
        reservation.setPassengerEmail("passenger@example.com");
        reservation.setId(1L);

        NotificationType type = NotificationType.RESERVATION_CONFIRMED;

        doNothing().when(mailSender).send(mailMessageCaptor.capture());

        strategy.notify(reservation, type);
        SimpleMailMessage mailMessage = mailMessageCaptor.getValue();

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        assertThat(mailMessage.getTo()).containsExactly("passenger@example.com");
        assertThat(mailMessage.getSubject()).isEqualTo("Reservation Confirmed");
        assertThat(mailMessage.getText()).contains("Your reservation has been confirmed.");
    }

    @Test
    void notify_nullEmail_shouldThrowException() {
        Reservation reservation = new Reservation();
        reservation.setPassengerEmail(null);
        reservation.setId(1L);

        NotificationType type = NotificationType.RESERVATION_CONFIRMED;

        assertThrows(IllegalArgumentException.class, () -> strategy.notify(reservation, type));
        verifyNoInteractions(mailSender);
    }
}

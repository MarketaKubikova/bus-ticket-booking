package com.example.busticketbooking.reservation.dto;

import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.model.Tariff;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ReservationResponse {
    private String origin;
    private String destination;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureDateTime;
    private int seatNumber;
    private String passengerEmail;
    private ReservationStatus status;
    private BigDecimal priceCzk;
    private Tariff tariff;
}

package com.example.busticketbooking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ReservationResponseDto {
    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private int seatNumber;
    private String passengerEmail;
}

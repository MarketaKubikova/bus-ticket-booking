package com.example.busticketbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(int seatNumber) {
        super("Seat number '" + seatNumber + "' is not available.");
    }
}

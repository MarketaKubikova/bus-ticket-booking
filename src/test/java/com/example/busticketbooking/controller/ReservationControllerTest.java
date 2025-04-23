package com.example.busticketbooking.controller;

import com.example.busticketbooking.common.exception.GlobalExceptionHandler;
import com.example.busticketbooking.reservation.controller.ReservationController;
import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class ReservationControllerTest {
    private static final String BASE_URL = "/api/reservations";

    private MockMvc mockMvc;
    @Mock
    private ReservationService reservationService;
    @InjectMocks
    private ReservationController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReservation_validRequest_returnsCreated() throws Exception {
        when(reservationService.createReservation(any(ReservationRequest.class))).thenReturn(new ReservationResponse("Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0, 0), 1, "test@test.com"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledTripId\": 1, \"seatNumber\": 1, \"passengerEmail\": \"test@test.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.origin").value("Prague"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.destination").value("Vienna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureDateTime").value("2025-01-01T11:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seatNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.passengerEmail").value("test@test.com"));
    }

    @Test
    void createReservation_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledTripId\": 1, \"seatNumber\": 1, \"passengerEmail\": null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

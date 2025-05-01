package com.example.busticketbooking.reservation.controller;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ReservationControllerIntegrationTest {
    private static final String BASE_URL = "/api/reservations";

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

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
                        .content("{\"scheduledTripId\": 1, \"seatNumber\": null, \"passengerEmail\": null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void getUsersReservations_validRequest_returnsReservation() throws Exception {
        when(reservationService.getUsersReservations(any())).thenReturn(List.of(new ReservationResponse("Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0), 1, "test@test.com")));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].origin").value("Prague"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].destination").value("Vienna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureDateTime").value("2025-01-01T11:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seatNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].passengerEmail").value("test@test.com"));
    }

    @Test
    void getUsersReservations_noUser_returnsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

package com.example.busticketbooking.reservation.controller;

import com.example.busticketbooking.reservation.dto.ReservationRequest;
import com.example.busticketbooking.reservation.dto.ReservationResponse;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.reservation.model.Tariff;
import com.example.busticketbooking.reservation.service.ReservationService;
import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.user.entity.AppUser;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ReservationControllerIntegrationTest {
    private static final String BASE_URL = "/api/v1/reservations";

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReservation_validRequest_returnsCreated() throws Exception {
        when(reservationService.createReservation(any(ReservationRequest.class))).thenReturn(new ReservationResponse("Prague", "Vienna", ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), ZoneId.of("Europe/Prague")), 1, "test@test.com", ReservationStatus.RESERVED, BigDecimal.TEN, Tariff.ADULT));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledTripId\": 1, \"seatNumber\": 1, \"passengerEmail\": \"test@test.com\", \"tariff\": \"ADULT\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.origin").value("Prague"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.destination").value("Vienna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureDateTime").value("2025-01-01T11:00:00+01:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seatNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.passengerEmail").value("test@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("RESERVED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priceCzk").value("10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tariff").value("ADULT"));
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
        when(reservationService.getUsersReservations(any())).thenReturn(List.of(new ReservationResponse("Prague", "Vienna", ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0), ZoneId.of("Europe/Prague")), 1, "test@test.com", ReservationStatus.RESERVED, BigDecimal.TEN, Tariff.ADULT)));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].origin").value("Prague"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].destination").value("Vienna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureDateTime").value("2025-01-01T11:00:00+01:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seatNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].passengerEmail").value("test@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("RESERVED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].priceCzk").value("10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tariff").value("ADULT"));
    }

    @Test
    void getUsersReservations_noUser_returnsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void cancelReservation_validRequest_returnsNoContent() throws Exception {
        doNothing().when(reservationService).cancelReservation(anyLong(), any(AppUser.class));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL)
                        .param("reservationId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void cancelReservation_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void cancelReservation_noUser_returnsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL)
                        .param("reservationId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

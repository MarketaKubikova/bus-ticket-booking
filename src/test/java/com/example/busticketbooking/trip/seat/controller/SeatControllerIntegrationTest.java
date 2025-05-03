package com.example.busticketbooking.trip.seat.controller;

import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.trip.seat.dto.SeatResponse;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.seat.service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class SeatControllerIntegrationTest {
    private static final String BASE_PATH = "/api/v1/seats";

    @MockitoBean
    private SeatService seatService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSeatsForScheduledTrip_withValidId_shouldReturnSeats() throws Exception {
        long scheduledTripId = 1L;

        when(seatService.getSeatsForScheduledTrip(scheduledTripId)).thenReturn(List.of(
                new SeatResponse(1, SeatStatus.FREE),
                new SeatResponse(2, SeatStatus.RESERVED)
        ));

        mockMvc.perform(get(BASE_PATH + "/" + scheduledTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].status").value("FREE"))
                .andExpect(jsonPath("$[1].seatNumber").value(2))
                .andExpect(jsonPath("$[1].status").value("RESERVED"));
    }

    @Test
    void getSeatsForScheduledTrip_noSeatsFound_shouldReturnNotFound() throws Exception {
        long scheduledTripId = 1L;

        when(seatService.getSeatsForScheduledTrip(scheduledTripId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_PATH + "/" + scheduledTripId))
                .andExpect(status().isNotFound());
    }
}

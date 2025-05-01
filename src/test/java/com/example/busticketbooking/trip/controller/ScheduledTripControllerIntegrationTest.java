package com.example.busticketbooking.trip.controller;

import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.trip.dto.ScheduledTripRequest;
import com.example.busticketbooking.trip.dto.ScheduledTripResponse;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.seat.dto.SeatResponse;
import com.example.busticketbooking.trip.seat.model.SeatStatus;
import com.example.busticketbooking.trip.service.ScheduledTripService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.*;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ScheduledTripControllerIntegrationTest {
    private static final String BASE_URL = "/api/scheduled-trips";
    private final ScheduledTripResponse scheduledTripResponse = new ScheduledTripResponse("101", "Prague", "Vienna", LocalDateTime.of(2025, 1, 1, 11, 0), LocalDateTime.of(2025, 1, 1, 15, 0), Set.of(new SeatResponse(1, SeatStatus.FREE)));

    @MockitoBean
    private ScheduledTripService scheduledTripService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getScheduledTripsByRouteAndDepartureDate_withAdminRoleAndValidRequest_shouldReturnScheduledTripList() throws Exception {
        when(scheduledTripService.getScheduledTripsByRouteAndDepartureDate(
                new RouteRequest("Prague", "Vienna", 334.0, Duration.ofHours(4)),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 5)))
                .thenReturn(List.of(scheduledTripResponse));

        mockMvc.perform(get(BASE_URL)
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-05")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origin\":\"Prague\",\"destination\":\"Vienna\", \"distance\":334.0,\"duration\":\"PT4H\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].busNumber").value("101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].origin").value("Prague"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].destination").value("Vienna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureDateTime").value("2025-01-01T11:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].arrivalDateTime").value("2025-01-01T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats[0].seatNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats[0].status").value("FREE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getScheduledTripsByRouteAndDepartureDate_withAdminRoleWithInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-05")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origin\":\" \",\"destination\":null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getScheduledTripsByRouteAndDepartureDate_withAdminRoleWithInvalidQuery_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("from", "2025-01-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origin\":\"Prague\",\"destination\":\"Vienna\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void getScheduledTripsByRouteAndDepartureDate_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-05")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origin\":\"Prague\",\"destination\":\"Vienna\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createScheduledTrip_withAdminRoleAndValidRequest_shouldReturnCreated() throws Exception {
        when(scheduledTripService.generateScheduledTripsByRule(
                new ScheduledTripRequest("101", "Prague", "Vienna", LocalTime.of(11, 0), Set.of(DayOfWeek.MONDAY), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 5))))
                .thenReturn(List.of(scheduledTripResponse));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\":\"101\",\"origin\":\"Prague\",\"destination\":\"Vienna\",\"fromDate\":\"2025-01-01\",\"toDate\":\"2025-01-05\",\"departureDay\":[\"MONDAY\"],\"departureTime\":\"11:00:00\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].busNumber").value("101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].origin").value("Prague"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].destination").value("Vienna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureDateTime").value("2025-01-01T11:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].arrivalDateTime").value("2025-01-01T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats[0].seatNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats[0].status").value("FREE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createScheduledTrip_withAdminRoleAndInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\":\" \",\"origin\":\"Prague\",\"destination\":\"Vienna\",\"fromDate\":\"2025-01-01\",\"toDate\":\"2025-01-05\",\"departureDay\":[\"MONDAY\"],\"departureTime\":\"11:00:00\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void createScheduledTrip_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\":\"101\",\"origin\":\"Prague\",\"destination\":\"Vienna\",\"fromDate\":\"2025-01-01\",\"toDate\":\"2025-01-05\",\"departureDay\":[\"MONDAY\"],\"departureTime\":\"11:00:00\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

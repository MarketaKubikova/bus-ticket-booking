package com.example.busticketbooking.bus.controller;

import com.example.busticketbooking.bus.dto.BusRequest;
import com.example.busticketbooking.bus.dto.BusResponse;
import com.example.busticketbooking.bus.seat.dto.SeatResponse;
import com.example.busticketbooking.bus.service.BusService;
import com.example.busticketbooking.common.exception.AlreadyExistsException;
import com.example.busticketbooking.common.exception.GlobalExceptionHandler;
import com.example.busticketbooking.common.exception.NotFoundException;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class BusControllerIntegrationTest {
    private static final String BASE_URL = "/api/buses";

    @MockitoBean
    private BusService busService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllBuses_withAdminRole_shouldReturnBusList() throws Exception {

        BusResponse bus1 = new BusResponse("101", 3, generateSeats(3));
        BusResponse bus2 = new BusResponse("102", 5, generateSeats(5));

        when(busService.getAllBuses()).thenReturn(List.of(bus1, bus2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].busNumber").value("101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].capacity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].busNumber").value("102"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].capacity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].seats").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].seats.length()").value(5));
    }

    @Test
    @WithMockUser(username = "user")
    void getAllBusesWithoutAdminRole_returnsForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBusesWithoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getBusById_withAdminRole_shouldReturnBusResponse() throws Exception {

        BusResponse bus = new BusResponse("101", 3, generateSeats(3));

        when(busService.getBusById(1L)).thenReturn(bus);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.busNumber").value("101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats.length()").value(3));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getBusById_withAdminRoleBusNotFound_shouldReturnNotFound() throws Exception {

        when(busService.getBusById(1L)).thenThrow(new NotFoundException("Bus with ID '1' not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void getBusById_withoutAdminRole_returnsForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBusById_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBus_withAdminRole_shouldReturnBusResponse() throws Exception {

        BusRequest request = new BusRequest("103", 2);
        BusResponse response = new BusResponse("103", 2, generateSeats(2));

        when(busService.createBus(request)).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\": \"103\", \"capacity\": 2}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.busNumber").value("103"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBus_withAdminRoleBusAlreadyExists_shouldReturnConflict() throws Exception {

        when(busService.createBus(any(BusRequest.class))).thenThrow(AlreadyExistsException.class);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\": \"101\", \"capacity\": 3}"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @WithMockUser(username = "user")
    void createBus_withoutAdminRole_returnsForbidden() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\": \"101\", \"capacity\": 3}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBus_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer invalid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"busNumber\": \"101\", \"capacity\": 3}"))
                .andExpect(status().isUnauthorized());
    }

    private Set<SeatResponse> generateSeats(int capacity) {
        return Stream.iterate(1, i -> i + 1)
                .limit(capacity)
                .map(i -> new SeatResponse(i, true))
                .collect(Collectors.toSet());
    }
}

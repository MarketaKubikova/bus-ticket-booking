package com.example.busticketbooking.trip.route.controller;

import com.example.busticketbooking.shared.exception.AlreadyExistsException;
import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.trip.route.dto.RouteRequest;
import com.example.busticketbooking.trip.route.dto.RouteResponse;
import com.example.busticketbooking.trip.route.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RouteControllerIntegrationTest {
    private static final String BASE_URL = "/api/routes";

    @MockitoBean
    private RouteService routeService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createRoute_withAdminRoleAndValidRequest_shouldReturnRouteResponse() throws Exception {
        when(routeService.createRoute(new RouteRequest("Prague", "Vienna", 334.0, Duration.ofHours(4))))
                .thenReturn(new RouteResponse("Prague", "Vienna", 334.0, Duration.ofHours(4)));

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"origin\": \"Prague\", \"destination\": \"Vienna\", \"distance\": 334.0, \"duration\": \"PT4H\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin").value("Prague"))
                .andExpect(jsonPath("$.destination").value("Vienna"))
                .andExpect(jsonPath("$.distance").value(334.0))
                .andExpect(jsonPath("$.duration").value("PT4H"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createRoute_withAdminRoleAndExistingRoute_shouldReturnConflict() throws Exception {
        when(routeService.createRoute(new RouteRequest("Prague", "Vienna", 334.0, Duration.ofHours(4))))
                .thenThrow(new AlreadyExistsException("Route already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"origin\": \"Prague\", \"destination\": \"Vienna\", \"distance\": 334.0, \"duration\": \"PT4H\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createRoute_withAdminRoleAndInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"origin\": \"\", \"destination\": \"Vienna\", \"distance\": 334.0, \"duration\": \"PT4H\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void createRoute_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"origin\": \"Prague\", \"destination\": \"Vienna\", \"distance\": 334.0, \"duration\": \"PT4H\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllRoutes_withAdminRole_shouldReturnRouteList() throws Exception {
        when(routeService.getAllRoutes())
                .thenReturn(List.of(new RouteResponse("Prague", "Vienna", 334.0, Duration.ofHours(4))));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].origin").value("Prague"))
                .andExpect(jsonPath("$[0].destination").value("Vienna"))
                .andExpect(jsonPath("$[0].distance").value(334.0))
                .andExpect(jsonPath("$[0].duration").value("PT4H"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllRoutes_withNoRoutes_shouldReturnNotFound() throws Exception {
        when(routeService.getAllRoutes()).thenThrow(new NotFoundException("No routes found"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void getAllRoutes_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllRoutes_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isUnauthorized());
    }
}

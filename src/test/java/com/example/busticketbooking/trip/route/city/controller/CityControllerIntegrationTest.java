package com.example.busticketbooking.trip.route.city.controller;

import com.example.busticketbooking.shared.exception.AlreadyExistsException;
import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.trip.route.city.dto.CityResponse;
import com.example.busticketbooking.trip.route.city.service.CityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CityControllerIntegrationTest {
    private static final String BASE_URL = "/api/cities";

    @MockitoBean
    private CityService cityService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCity_withAdminRole_shouldReturnCreatedCity() throws Exception {
        when(cityService.createCity(any())).thenReturn(new CityResponse("New York"));

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"name\": \"New York\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New York"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCity_withExistingCity_shouldReturnConflict() throws Exception {
        when(cityService.createCity(any())).thenThrow(AlreadyExistsException.class);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"name\": \"New York\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCity_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void createCity_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content("{\"name\": \"New York\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllCities_withAdminRole_shouldReturnCityList() throws Exception {
        when(cityService.getAllCities()).thenReturn(List.of(new CityResponse("New York"), new CityResponse("Los Angeles")));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("New York"))
                .andExpect(jsonPath("$[1].name").value("Los Angeles"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllCities_withNoCities_shouldReturnNotFound() throws Exception {
        when(cityService.getAllCities()).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void getAllCities_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isForbidden());
    }
}

package com.example.busticketbooking.user.controller;

import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.shared.exception.TooManyRequestsException;
import com.example.busticketbooking.user.dto.AuthResponse;
import com.example.busticketbooking.user.dto.LoginRequest;
import com.example.busticketbooking.user.dto.RegisterRequest;
import com.example.busticketbooking.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AuthControllerTest {
    private static final String BASE_URL = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    void register_validRequest_returnsOk() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password");
        AuthResponse response = new AuthResponse("token");
        when(userService.register(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token"));
    }

    @Test
    void register_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void login_validRequest_returnsOk() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password");
        AuthResponse response = new AuthResponse("token");
        when(userService.login(eq(request), any(HttpServletRequest.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token"));
    }

    @Test
    void login_userDoesNotExist_returns() throws Exception {
        LoginRequest request = new LoginRequest("nonexistuser", "password");
        when(userService.login(eq(request), any(HttpServletRequest.class))).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"nonexistuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void login_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"\", \"password\": null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void login_rateLimitExceeded_returnsTooManyRequests() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password");
        when(userService.login(eq(request), any(HttpServletRequest.class))).thenThrow(new TooManyRequestsException());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }
}

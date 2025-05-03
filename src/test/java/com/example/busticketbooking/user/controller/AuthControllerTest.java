package com.example.busticketbooking.user.controller;

import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.user.dto.AuthResponse;
import com.example.busticketbooking.user.dto.LoginRequest;
import com.example.busticketbooking.user.dto.RegisterRequest;
import com.example.busticketbooking.user.service.UserService;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_validRequest_returnsOk() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password");
        AuthResponse response = new AuthResponse("token");
        when(userService.register(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token"));
    }

    @Test
    void register_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void login_validRequest_returnsOk() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password");
        AuthResponse response = new AuthResponse("token");
        when(userService.login(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token"));
    }

    @Test
    void login_userDoesNotExist_returns() throws Exception {
        LoginRequest request = new LoginRequest("nonexistuser", "password");
        when(userService.login(request)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"nonexistuser\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void login_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"\", \"password\": null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

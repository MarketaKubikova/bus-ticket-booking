package com.example.busticketbooking.user;

import com.example.busticketbooking.auth.JwtService;
import com.example.busticketbooking.auth.dto.AuthResponse;
import com.example.busticketbooking.auth.dto.LoginRequest;
import com.example.busticketbooking.auth.dto.RegisterRequest;
import com.example.busticketbooking.config.PasswordConfig;
import com.example.busticketbooking.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordConfig passwordConfig;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private UserService userService;

    @Test
    void register_validRequest_shouldReturnAuthResponse() {
        RegisterRequest request = new RegisterRequest("testuser", "password");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(AppUser.class))).thenReturn(new AppUser());
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("generated-token");

        AuthResponse response = userService.register(request);

        assertThat(response.token()).isEqualTo("generated-token");
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(jwtService, times(1)).generateToken(any(AppUser.class));
    }

    @Test
    void register_usernameExists_shouldThrowException() {
        RegisterRequest request = new RegisterRequest("testuser", "password");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(request));
    }

    @Test
    void login_validRequest_shouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest("testuser", "password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generated-token");

        AuthResponse response = userService.login(request);

        assertThat(response.token()).isEqualTo("generated-token");
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(any());
    }
}

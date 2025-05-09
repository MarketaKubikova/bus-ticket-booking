package com.example.busticketbooking.user.service;

import com.example.busticketbooking.shared.config.PasswordConfig;
import com.example.busticketbooking.shared.exception.AlreadyExistsException;
import com.example.busticketbooking.shared.exception.TooManyRequestsException;
import com.example.busticketbooking.shared.security.JwtService;
import com.example.busticketbooking.shared.security.LoginRateLimiter;
import com.example.busticketbooking.user.dto.AuthResponse;
import com.example.busticketbooking.user.dto.LoginRequest;
import com.example.busticketbooking.user.dto.RegisterRequest;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.model.Role;
import com.example.busticketbooking.user.repository.UserRepository;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

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
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private LoginRateLimiter rateLimiter;
    @Mock
    private Bucket bucket;
    @Mock
    private SecurityContext securityContext;
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

        assertThrows(AlreadyExistsException.class, () -> userService.register(request));
    }

    @Test
    void login_validRequest_shouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest("testuser", "password");

        when(servletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(anyLong())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generated-token");

        AuthResponse response = userService.login(request, servletRequest);

        assertThat(response.token()).isEqualTo("generated-token");
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(any());
    }

    @Test
    void login_tooManyRequests_shouldReturnThrowException() {
        LoginRequest request = new LoginRequest("testuser", "password");

        when(servletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(anyLong())).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> userService.login(request, servletRequest));
    }

    @Test
    void getCurrentAuthenticatedUser_userLoggedIn_shouldReturnUser() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setReservations(new HashSet<>());

        when(authentication.getName()).thenReturn(user.getUsername());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        AppUser result = userService.getCurrentAuthenticatedUser();

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void getCurrentAuthenticatedUser_userNotLoggedIn_shouldReturnNull() {
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AppUser user = userService.getCurrentAuthenticatedUser();

        assertThat(user).isNull();
        verifyNoInteractions(userRepository);
    }
}

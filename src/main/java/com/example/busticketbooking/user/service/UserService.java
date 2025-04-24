package com.example.busticketbooking.user.service;

import com.example.busticketbooking.common.config.PasswordConfig;
import com.example.busticketbooking.common.exception.AlreadyExistsException;
import com.example.busticketbooking.common.security.JwtService;
import com.example.busticketbooking.user.dto.AuthResponse;
import com.example.busticketbooking.user.dto.LoginRequest;
import com.example.busticketbooking.user.dto.RegisterRequest;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.model.Role;
import com.example.busticketbooking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new AlreadyExistsException("User with username '" + request.username() + "' already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordConfig.passwordEncoder().encode(request.password()));
        user.setRole(Role.USER);

        AppUser saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(user);

        return new AuthResponse(jwt);
    }
}

package com.example.busticketbooking.user;

import com.example.busticketbooking.auth.JwtService;
import com.example.busticketbooking.auth.dto.AuthResponse;
import com.example.busticketbooking.auth.dto.LoginRequest;
import com.example.busticketbooking.auth.dto.RegisterRequest;
import com.example.busticketbooking.config.PasswordConfig;
import com.example.busticketbooking.exception.UsernameAlreadyExistsException;
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
            throw new UsernameAlreadyExistsException();
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

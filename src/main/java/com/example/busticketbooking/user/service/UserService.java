package com.example.busticketbooking.user.service;

import com.example.busticketbooking.payment.entity.Wallet;
import com.example.busticketbooking.payment.service.WalletService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final LoginRateLimiter rateLimiter;

    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final JwtService jwtService;
    private final WalletService walletService;
    private final AuthenticationManager authenticationManager;
    @Value("${rate.limit.login.tokenCost}")
    private int tokenCost;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.email())) {
            throw new AlreadyExistsException("User with username '" + request.email() + "' already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(request.email());
        user.setPassword(passwordConfig.passwordEncoder().encode(request.password()));
        user.setEmail(request.email());
        user.setRole(Role.USER);

        userRepository.save(user);

        Wallet wallet = walletService.createWallet(user);
        user.setWallet(wallet);

        AppUser savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        Bucket bucket = rateLimiter.resolveBucket(ip);

        if (bucket.tryConsume(tokenCost)) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails user = (UserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(user);

            return new AuthResponse(jwt);
        } else {
            throw new TooManyRequestsException();
        }
    }

    public AppUser getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

package com.example.busticketbooking.shared.config;

import com.example.busticketbooking.shared.security.JwtAuthenticationFilter;
import com.example.busticketbooking.user.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AppUserDetailsService userDetailsService;
    private final PasswordConfig passwordConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment env) throws Exception {
        String[] openapiPaths = {"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs.yaml"};

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                            if (Arrays.asList(env.getActiveProfiles()).contains("local")) {
                                auth.requestMatchers(openapiPaths).permitAll();
                            }
                            auth
                                    .requestMatchers("/actuator/health").permitAll()
                                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                                    .requestMatchers("/api/v1/auth/**").permitAll()
                                    .requestMatchers((HttpMethod.POST), "/api/v1/reservations").permitAll()
                                    .requestMatchers((HttpMethod.GET), "/api/v1/scheduled-trips/search").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/api/v1/seats/**").permitAll()
                                    .requestMatchers(HttpMethod.POST, "/api/v1/pay").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/api/v1/cities").permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordConfig.passwordEncoder());

        return new ProviderManager(provider);
    }
}

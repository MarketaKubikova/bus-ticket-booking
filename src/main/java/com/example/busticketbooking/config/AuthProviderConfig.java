package com.example.busticketbooking.config;

import com.example.busticketbooking.user.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    private final AppUserDetailsService userDetailsService;
    private final PasswordConfig passwordConfig;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return provider;
    }
}

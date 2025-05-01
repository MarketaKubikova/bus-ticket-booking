package com.example.busticketbooking.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static com.example.busticketbooking.shared.util.Constant.ZONE_PRAGUE;

@Configuration
public class AppConfig {
    @Bean
    public Clock clock() {
        return Clock.system(ZONE_PRAGUE);
    }
}

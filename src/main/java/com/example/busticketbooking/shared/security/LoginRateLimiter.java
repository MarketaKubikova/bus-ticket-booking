package com.example.busticketbooking.shared.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LoginRateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    @Value("${rate.limit.login.maxAttempts}")
    private int maxAttempts;
    @Value("${rate.limit.login.minutes}")
    private int minutes;

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.classic(maxAttempts, Refill.greedy(maxAttempts, Duration.ofMinutes(minutes))))
                        .build()
        );
    }
}


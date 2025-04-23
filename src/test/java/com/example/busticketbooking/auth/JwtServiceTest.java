package com.example.busticketbooking.auth;

import com.example.busticketbooking.common.security.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private final String testSecret = Base64.getEncoder()
            .encodeToString("test-secret-key-should-be-very-secure" .getBytes());
    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", 1);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UserDetails user = new User("testuser", "testpassword", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull().isNotEmpty();

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void shouldInvalidateExpiredToken() {
        UserDetails user = new User("testuser", "testpassword", List.of());
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(testSecret)), SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtService.isTokenValid(token, user)).isFalse();
    }

    @Test
    void shouldThrowOnMalformedToken() {
        String badToken = "not.a.valid.token";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(badToken));
    }
}

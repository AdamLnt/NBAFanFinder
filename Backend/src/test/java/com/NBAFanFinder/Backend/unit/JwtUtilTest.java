package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.NBAFanFinder.Backend.Security.JwtUtil;

@DisplayName("Tests unitaires - JwtUtil")
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "monSecretSuperLongPourJWTQuiFaitAuMoins256Bits123456789");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    @DisplayName("Génère un token JWT non null et valide")
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken("user@email.com");

        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Récupère l'email à partir du token")
    void shouldExtractUsernameFromToken() {
        String token = jwtUtil.generateToken("user@email.com");

        String username = jwtUtil.getUsernameFromToken(token);

        assertThat(username).isEqualTo("user@email.com");
    }

    @Test
    @DisplayName("Refuse un token mal formé")
    void shouldRejectInvalidToken() {
        assertThat(jwtUtil.validateToken("not.a.valid.token")).isFalse();
    }

    @Test
    @DisplayName("Refuse un token expiré")
    void shouldRejectExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String expiredToken = jwtUtil.generateToken("user@email.com");

        assertThat(jwtUtil.validateToken(expiredToken)).isFalse();
    }
}

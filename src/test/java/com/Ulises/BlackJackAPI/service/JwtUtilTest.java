package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
            "blackjack-api-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("testuser", 1L);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken("testuser", 1L);
        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testExtractPlayerId() {
        String token = jwtUtil.generateToken("testuser", 42L);
        Long playerId = jwtUtil.extractPlayerId(token);

        assertEquals(42L, playerId);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("testuser", 1L);

        assertTrue(jwtUtil.validateToken(token, "testuser"));
        assertFalse(jwtUtil.validateToken(token, "otheruser"));
    }

    @Test
    void testTokenExpiration() {
        String token = jwtUtil.generateToken("testuser", 1L);

        assertFalse(jwtUtil.isTokenExpired(token));
    }
}
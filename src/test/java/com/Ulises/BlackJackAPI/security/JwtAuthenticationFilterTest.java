package com.Ulises.BlackJackAPI.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtUtil createJwtUtil() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();
        Field secretField = jwtUtil.getClass().getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "dGhpcy1pcy1hLXNlY3VyZS1qd3Qtc2VjcmV0LWtleS1mb3ItYmxhY2tqYWNrLWFwaS1wcm9qZWN0LTI1Ng==");

        Field expField = jwtUtil.getClass().getDeclaredField("expiration");
        expField.setAccessible(true);
        expField.set(jwtUtil, 86400000L);

        return jwtUtil;
    }

    @Test
    void testJwtUtilExtractUsername() throws Exception {
        JwtUtil jwtUtil = createJwtUtil();

        String token = jwtUtil.generateToken("testuser", 1L);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testJwtUtilExtractPlayerId() throws Exception {
        JwtUtil jwtUtil = createJwtUtil();

        String token = jwtUtil.generateToken("testuser", 1L);
        Long playerId = jwtUtil.extractPlayerId(token);
        assertEquals(1L, playerId);
    }

    @Test
    void testJwtUtilValidateToken() throws Exception {
        JwtUtil jwtUtil = createJwtUtil();

        String token = jwtUtil.generateToken("testuser", 1L);
        assertTrue(jwtUtil.validateToken(token, "testuser"));
        assertFalse(jwtUtil.validateToken(token, "otheruser"));
    }

    @Test
    void testJwtUtilTokenExpiration() throws Exception {
        JwtUtil jwtUtil = createJwtUtil();

        String token = jwtUtil.generateToken("testuser", 1L);
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testJwtUtilGenerateToken() throws Exception {
        JwtUtil jwtUtil = createJwtUtil();

        String token = jwtUtil.generateToken("newuser", 99L);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}
package com.Ulises.BlackJackAPI.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testSuccessResponse() {
        ApiResponse<String> response = ApiResponse.success("test data");

        assertEquals(200, response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals("test data", response.getData());
    }

    @Test
    void testSuccessWithCustomMessage() {
        ApiResponse<String> response = ApiResponse.success("Custom message", "data");

        assertEquals(200, response.getCode());
        assertEquals("Custom message", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void testErrorResponse() {
        ApiResponse<Void> response = ApiResponse.error(404, "Not found");

        assertEquals(404, response.getCode());
        assertEquals("Not found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testAuthResponseCreation() {
        AuthResponse response = new AuthResponse("token123", 1L, "player1", 1000);

        assertEquals("token123", response.getToken());
        assertEquals(1L, response.getPlayerId());
        assertEquals("player1", response.getUsername());
        assertEquals(1000, response.getScore());
    }

    @Test
    void testPlayerResponseCreation() {
        PlayerResponse response = new PlayerResponse(1L, "player1", 1500);

        assertEquals(1L, response.getId());
        assertEquals("player1", response.getUsername());
        assertEquals(1500, response.getScore());
    }

    @Test
    void testPlayRequestCreation() {
        PlayRequest request = new PlayRequest("HIT", 100, 0);

        assertEquals("HIT", request.getAction());
        assertEquals(100, request.getBet());
        assertEquals(0, request.getHandIndex());
    }
}
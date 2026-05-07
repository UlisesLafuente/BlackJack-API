package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;

/**
 * DTO for authentication responses.
 * Contains JWT token and player information after login/register.
 *
 * @author Ulises Lafuente
 */
@Getter
public class AuthResponse {
    private String token;
    private Long playerId;
    private String username;
    private Integer score;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long playerId, String username, Integer score) {
        this.token = token;
        this.playerId = playerId;
        this.username = username;
        this.score = score;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
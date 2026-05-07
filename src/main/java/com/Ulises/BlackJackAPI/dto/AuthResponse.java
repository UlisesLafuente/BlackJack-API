package com.Ulises.BlackJackAPI.dto;

public class AuthResponse {
    private String token;
    private Long playerId;
    private String username;
    private Integer score;

    public AuthResponse() {}

    public AuthResponse(String token, Long playerId, String username, Integer score) {
        this.token = token;
        this.playerId = playerId;
        this.username = username;
        this.score = score;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
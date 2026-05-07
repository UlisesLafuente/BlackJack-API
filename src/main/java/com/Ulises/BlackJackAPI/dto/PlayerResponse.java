package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;

/**
 * DTO for player information responses.
 *
 * @author Ulises Lafuente
 */
@Getter
public class PlayerResponse {
    private Long id;
    private String username;
    private Integer score;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String username, Integer score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
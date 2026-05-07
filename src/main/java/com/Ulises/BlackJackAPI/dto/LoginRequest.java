package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;

/**
 * DTO for player login requests.
 *
 * @author Ulises Lafuente
 */
@Getter
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
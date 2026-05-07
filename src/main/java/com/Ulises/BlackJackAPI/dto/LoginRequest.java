package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for player login requests.
 *
 * @author Ulises Lafuente
 */
@Setter
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

}
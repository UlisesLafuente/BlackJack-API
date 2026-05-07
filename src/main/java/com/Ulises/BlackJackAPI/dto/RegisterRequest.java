package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for player registration requests.
 *
 * @author Ulises Lafuente
 */
@Setter
@Getter
public class RegisterRequest {
    private String username;
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
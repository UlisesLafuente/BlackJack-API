package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;

/**
 * DTO for updating player username requests.
 *
 * @author Ulises Lafuente
 */
@Getter
public class UpdateUsernameRequest {
    private String username;

    public UpdateUsernameRequest() {
    }

    public UpdateUsernameRequest(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
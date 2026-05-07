package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating player username requests.
 *
 * @author Ulises Lafuente
 */
@Setter
@Getter
public class UpdateUsernameRequest {
    private String username;

    public UpdateUsernameRequest() {
    }

    public UpdateUsernameRequest(String username) {
        this.username = username;
    }

}
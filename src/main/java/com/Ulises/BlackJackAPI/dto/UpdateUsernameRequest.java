package com.Ulises.BlackJackAPI.dto;

/**
 * DTO for updating player username requests.
 *
 * @author Ulises Lafuente
 */
public class UpdateUsernameRequest {
    private String username;

    public UpdateUsernameRequest() {}

    public UpdateUsernameRequest(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
package com.Ulises.BlackJackAPI.exception;

/**
 * Exception thrown when a player is not found in the database.
 *
 * @author Ulises Lafuente
 */
public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
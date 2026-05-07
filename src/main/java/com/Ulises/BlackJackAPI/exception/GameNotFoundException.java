package com.Ulises.BlackJackAPI.exception;

/**
 * Exception thrown when a game is not found in the database.
 *
 * @author Ulises Lafuente
 */
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
package com.Ulises.BlackJackAPI.exception;

/**
 * Exception thrown when an invalid game move is attempted.
 *
 * @author Ulises Lafuente
 */
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
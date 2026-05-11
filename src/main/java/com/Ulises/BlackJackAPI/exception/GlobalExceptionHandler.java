package com.Ulises.BlackJackAPI.exception;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import com.Ulises.BlackJackAPI.exception.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * Global exception handler for the API.
 * Handles all exceptions and returns standardized error responses.
 *
 * @author Ulises Lafuente
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handlePlayerNotFound(PlayerNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage())));
    }

    @ExceptionHandler(GameNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGameNotFound(GameNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage())));
    }

    @ExceptionHandler(InvalidMoveException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidMove(InvalidMoveException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getMessage())));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "Invalid credentials")));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Validation failed: " + errors)));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleRuntimeException(RuntimeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Internal server error: " + ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleException(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "An unexpected error occurred")));
    }
}
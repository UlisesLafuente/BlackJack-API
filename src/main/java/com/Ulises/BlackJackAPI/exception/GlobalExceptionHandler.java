package com.Ulises.BlackJackAPI.exception;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

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
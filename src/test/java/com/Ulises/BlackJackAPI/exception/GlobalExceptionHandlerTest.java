package com.Ulises.BlackJackAPI.exception;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandlePlayerNotFound() {
        PlayerNotFoundException ex = new PlayerNotFoundException("Player not found");
        Mono<ResponseEntity<ApiResponse<Void>>> result = handler.handlePlayerNotFound(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(404, response.getBody().getCode());
                    assertEquals("Player not found", response.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void testHandleGameNotFound() {
        GameNotFoundException ex = new GameNotFoundException("Game not found");
        Mono<ResponseEntity<ApiResponse<Void>>> result = handler.handleGameNotFound(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(404, response.getBody().getCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleInvalidMove() {
        InvalidMoveException ex = new InvalidMoveException("Invalid action");
        Mono<ResponseEntity<ApiResponse<Void>>> result = handler.handleInvalidMove(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(400, response.getBody().getCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleInvalidCredentials() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
        Mono<ResponseEntity<ApiResponse<Void>>> result = handler.handleInvalidCredentials(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(401, response.getBody().getCode());
                    assertEquals("Invalid credentials", response.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test error");
        Mono<ResponseEntity<ApiResponse<Void>>> result = handler.handleRuntimeException(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertTrue(response.getBody().getMessage().contains("Internal server error"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected");
        Mono<ResponseEntity<ApiResponse<Void>>> result = handler.handleException(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(500, response.getBody().getCode());
                    assertEquals("An unexpected error occurred", response.getBody().getMessage());
                })
                .verifyComplete();
    }
}
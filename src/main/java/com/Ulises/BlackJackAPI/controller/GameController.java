package com.Ulises.BlackJackAPI.controller;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import com.Ulises.BlackJackAPI.dto.GameResponse;
import com.Ulises.BlackJackAPI.dto.PlayRequest;
import com.Ulises.BlackJackAPI.security.PlayerUserDetails;
import com.Ulises.BlackJackAPI.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public Mono<ResponseEntity<ApiResponse<GameResponse>>> createGame(
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        Long playerId = userDetails.getPlayerId();
        return gameService.createGame(playerId)
                .map(game -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Game created successfully", game)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<GameResponse>>> getGame(
            @PathVariable Long id,
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        Long playerId = userDetails.getPlayerId();
        return gameService.getGame(id, playerId)
                .map(game -> ResponseEntity.ok(ApiResponse.success(game)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/play")
    public Mono<ResponseEntity<ApiResponse<GameResponse>>> play(
            @PathVariable Long id,
            @RequestBody PlayRequest request,
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        Long playerId = userDetails.getPlayerId();
        return gameService.play(id, playerId, request)
                .map(game -> ResponseEntity.ok(ApiResponse.success(game)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteGame(
            @PathVariable Long id,
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        Long playerId = userDetails.getPlayerId();
        return gameService.deleteGame(id, playerId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
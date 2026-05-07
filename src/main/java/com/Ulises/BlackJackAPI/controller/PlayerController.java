package com.Ulises.BlackJackAPI.controller;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import com.Ulises.BlackJackAPI.dto.PlayerResponse;
import com.Ulises.BlackJackAPI.dto.UpdateUsernameRequest;
import com.Ulises.BlackJackAPI.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for player operations.
 * Handles player profile updates.
 *
 * @author Ulises Lafuente
 */
@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PutMapping("/{playerId}")
    public Mono<ResponseEntity<ApiResponse<PlayerResponse>>> updateUsername(
            @PathVariable Long playerId,
            @RequestBody UpdateUsernameRequest request) {
        return playerService.updateUsername(playerId, request.getUsername())
                .map(player -> ResponseEntity.ok(ApiResponse.success("Username updated", player)));
    }
}
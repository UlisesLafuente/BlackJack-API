package com.Ulises.BlackJackAPI.controller;

import com.Ulises.BlackJackAPI.dto.ApiResponse;
import com.Ulises.BlackJackAPI.dto.RankingResponse;
import com.Ulises.BlackJackAPI.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    private final PlayerService playerService;

    public RankingController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<RankingResponse>>> getRanking() {
        return playerService.getRanking()
                .map(ranking -> ResponseEntity.ok(ApiResponse.success(ranking)));
    }
}
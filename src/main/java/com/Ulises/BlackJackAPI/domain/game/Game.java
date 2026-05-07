package com.Ulises.BlackJackAPI.domain.game;

import com.Ulises.BlackJackAPI.model.enums.GameResult;
import com.Ulises.BlackJackAPI.model.enums.GameStatus;

import java.time.LocalDateTime;

public class Game {
    private Long id;
    private Long playerId;
    private Integer bet;
    private Integer insuranceBet;
    private GameStatus status;
    private GameResult result;
    private Integer playerScore;
    private Integer croupierScore;
    private LocalDateTime createdAt;

    public Game() {}

    public Game(Long playerId) {
        this.playerId = playerId;
        this.bet = 0;
        this.insuranceBet = 0;
        this.status = GameStatus.BETTING;
        this.result = null;
        this.playerScore = 0;
        this.croupierScore = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public Integer getBet() { return bet; }
    public void setBet(Integer bet) { this.bet = bet; }
    public Integer getInsuranceBet() { return insuranceBet; }
    public void setInsuranceBet(Integer insuranceBet) { this.insuranceBet = insuranceBet; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    public GameResult getResult() { return result; }
    public void setResult(GameResult result) { this.result = result; }
    public Integer getPlayerScore() { return playerScore; }
    public void setPlayerScore(Integer playerScore) { this.playerScore = playerScore; }
    public Integer getCroupierScore() { return croupierScore; }
    public void setCroupierScore(Integer croupierScore) { this.croupierScore = croupierScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void placeBet(int amount) {
        this.bet = amount;
        this.status = GameStatus.PLAYER_TURN;
    }

    public void buyInsurance(int amount) {
        this.insuranceBet = amount;
    }

    public boolean canPlaceBet() {
        return this.status == GameStatus.BETTING;
    }

    public boolean canBuyInsurance() {
        return this.status == GameStatus.PLAYER_TURN && this.insuranceBet == 0;
    }

    public boolean canPlay() {
        return this.status == GameStatus.PLAYER_TURN;
    }

    public void finish(GameResult result, int playerScore, int croupierScore) {
        this.result = result;
        this.status = GameStatus.FINISHED;
        this.playerScore = playerScore;
        this.croupierScore = croupierScore;
    }
}
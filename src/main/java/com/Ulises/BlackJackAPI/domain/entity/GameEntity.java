package com.Ulises.BlackJackAPI.domain.entity;

import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("games")
public class GameEntity {
    @Id
    private Long id;

    @Column("player_id")
    private Long playerId;

    @Column("bet")
    private Integer bet;

    @Column("insurance_bet")
    private Integer insuranceBet;

    @Column("status")
    private GameStatus status;

    @Column("result")
    private GameResult result;

    @Column("player_score")
    private Integer playerScore;

    @Column("croupier_score")
    private Integer croupierScore;

    @Column("created_at")
    private LocalDateTime createdAt;

    public GameEntity() {}

    public GameEntity(Long playerId) {
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
}
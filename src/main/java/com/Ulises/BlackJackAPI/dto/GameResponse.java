package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.model.enums.GameResult;
import com.Ulises.BlackJackAPI.model.enums.GameStatus;
import java.util.List;

public class GameResponse {
    private Long id;
    private Long playerId;
    private Integer bet;
    private Integer insuranceBet;
    private GameStatus status;
    private GameResult result;
    private Integer playerScore;
    private Integer croupierScore;
    private List<HandResponse> hands;
    private String message;

    public GameResponse() {}

    public GameResponse(Long id, Long playerId, Integer bet, Integer insuranceBet, GameStatus status,
                       GameResult result, Integer playerScore, Integer croupierScore,
                       List<HandResponse> hands, String message) {
        this.id = id;
        this.playerId = playerId;
        this.bet = bet;
        this.insuranceBet = insuranceBet;
        this.status = status;
        this.result = result;
        this.playerScore = playerScore;
        this.croupierScore = croupierScore;
        this.hands = hands;
        this.message = message;
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
    public List<HandResponse> getHands() { return hands; }
    public void setHands(List<HandResponse> hands) { this.hands = hands; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
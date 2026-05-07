package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import lombok.Getter;

import java.util.List;

/**
 * DTO representing a game state response.
 * Contains game details including bets, scores, and hands.
 *
 * @author Ulises Lafuente
 */
@Getter
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

    public GameResponse() {
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public void setBet(Integer bet) {
        this.bet = bet;
    }

    public void setInsuranceBet(Integer insuranceBet) {
        this.insuranceBet = insuranceBet;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public void setPlayerScore(Integer playerScore) {
        this.playerScore = playerScore;
    }

    public void setCroupierScore(Integer croupierScore) {
        this.croupierScore = croupierScore;
    }

    public void setHands(List<HandResponse> hands) {
        this.hands = hands;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO representing a game state response.
 * Contains game details including bets, scores, and hands.
 *
 * @author Ulises Lafuente
 */
@Setter
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

}
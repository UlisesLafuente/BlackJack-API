package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for game play action requests.
 * Contains the action type (BET, HIT, STAND, etc.), bet amount, and hand index.
 *
 * @author Ulises Lafuente
 */
@Setter
@Getter
public class PlayRequest {
    private String action;
    private Integer bet;
    private Integer handIndex;

    public PlayRequest() {
    }

    public PlayRequest(String action, Integer bet, Integer handIndex) {
        this.action = action;
        this.bet = bet;
        this.handIndex = handIndex;
    }

}
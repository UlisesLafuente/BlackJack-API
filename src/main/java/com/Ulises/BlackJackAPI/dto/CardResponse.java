package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for card responses.
 * Contains card details including suit, rank, value, and hidden state.
 *
 * @author Ulises Lafuente
 */
@Setter
@Getter
public class CardResponse {
    private Suit suit;
    private Rank rank;
    private int value;
    private boolean isHidden;

    public CardResponse() {
    }

    public CardResponse(Suit suit, Rank rank, int value, boolean isHidden) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
        this.isHidden = isHidden;
    }

}
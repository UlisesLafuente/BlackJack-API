package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import lombok.Getter;

/**
 * DTO for card responses.
 * Contains card details including suit, rank, value, and hidden state.
 *
 * @author Ulises Lafuente
 */
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

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
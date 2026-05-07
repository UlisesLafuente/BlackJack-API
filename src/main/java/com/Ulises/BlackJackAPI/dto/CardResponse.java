package com.Ulises.BlackJackAPI.dto;

import com.Ulises.BlackJackAPI.model.enums.Rank;
import com.Ulises.BlackJackAPI.model.enums.Suit;

public class CardResponse {
    private Suit suit;
    private Rank rank;
    private int value;
    private boolean isHidden;

    public CardResponse() {}

    public CardResponse(Suit suit, Rank rank, int value, boolean isHidden) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
        this.isHidden = isHidden;
    }

    public Suit getSuit() { return suit; }
    public void setSuit(Suit suit) { this.suit = suit; }
    public Rank getRank() { return rank; }
    public void setRank(Rank rank) { this.rank = rank; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { isHidden = hidden; }
}
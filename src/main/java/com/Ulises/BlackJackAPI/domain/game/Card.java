package com.Ulises.BlackJackAPI.domain.game;

import com.Ulises.BlackJackAPI.model.enums.Rank;
import com.Ulises.BlackJackAPI.model.enums.Suit;

public class Card {
    private Long id;
    private Long handId;
    private Suit suit;
    private Rank rank;
    private int value;
    private boolean hidden;

    public Card() {}

    public Card(Suit suit, Rank rank, boolean hidden) {
        this.suit = suit;
        this.rank = rank;
        this.value = rank.getValue();
        this.hidden = hidden;
    }

    public Card(Long handId, Suit suit, Rank rank, boolean hidden) {
        this.handId = handId;
        this.suit = suit;
        this.rank = rank;
        this.value = rank.getValue();
        this.hidden = hidden;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHandId() { return handId; }
    public void setHandId(Long handId) { this.handId = handId; }
    public Suit getSuit() { return suit; }
    public void setSuit(Suit suit) { this.suit = suit; }
    public Rank getRank() { return rank; }
    public void setRank(Rank rank) { this.rank = rank; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public boolean isAce() {
        return this.rank == Rank.ACE;
    }

    public boolean isFaceCard() {
        return this.rank == Rank.JACK || this.rank == Rank.QUEEN || this.rank == Rank.KING;
    }
}
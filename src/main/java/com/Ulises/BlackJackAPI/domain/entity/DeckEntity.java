package com.Ulises.BlackJackAPI.domain.entity;

import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("decks")
public class DeckEntity {
    @Id
    private Long id;

    @Column("game_id")
    private Long gameId;

    @Column("suit")
    private Suit suit;

    @Column("card_rank")
    private String rank;

    @Column("value")
    private Integer value;

    @Column("drawn")
    private Boolean drawn;

    public DeckEntity() {}

    public DeckEntity(Long gameId, Suit suit, Rank rank) {
        this.gameId = gameId;
        this.suit = suit;
        this.rank = rank.name();
        this.value = rank.getValue();
        this.drawn = false;
    }

    public Rank getRankEnum() {
        return rank != null ? Rank.valueOf(rank) : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    public Suit getSuit() { return suit; }
    public void setSuit(Suit suit) { this.suit = suit; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    public Boolean getDrawn() { return drawn; }
    public void setDrawn(Boolean drawn) { this.drawn = drawn; }
}
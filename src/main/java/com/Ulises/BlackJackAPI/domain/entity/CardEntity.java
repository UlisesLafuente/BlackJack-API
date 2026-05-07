package com.Ulises.BlackJackAPI.domain.entity;

import com.Ulises.BlackJackAPI.domain.enums.Rank;
import com.Ulises.BlackJackAPI.domain.enums.Suit;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cards")
public class CardEntity {
    @Id
    private Long id;

    @Column("hand_id")
    private Long handId;

    @Column("suit")
    private Suit suit;

    @Column("card_rank")
    private Rank rank;

    @Column("value")
    private Integer value;

    @Column("is_hidden")
    private Boolean isHidden;

    public CardEntity() {
    }

    public CardEntity(Long handId, Suit suit, Rank rank, Boolean isHidden) {
        this.handId = handId;
        this.suit = suit;
        this.rank = rank;
        this.value = rank.getValue();
        this.isHidden = isHidden;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHandId() {
        return handId;
    }

    public void setHandId(Long handId) {
        this.handId = handId;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
}
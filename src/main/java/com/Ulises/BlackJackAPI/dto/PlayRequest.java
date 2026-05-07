package com.Ulises.BlackJackAPI.dto;

public class PlayRequest {
    private String action;
    private Integer bet;
    private Integer handIndex;

    public PlayRequest() {}

    public PlayRequest(String action, Integer bet, Integer handIndex) {
        this.action = action;
        this.bet = bet;
        this.handIndex = handIndex;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Integer getBet() { return bet; }
    public void setBet(Integer bet) { this.bet = bet; }
    public Integer getHandIndex() { return handIndex; }
    public void setHandIndex(Integer handIndex) { this.handIndex = handIndex; }
}
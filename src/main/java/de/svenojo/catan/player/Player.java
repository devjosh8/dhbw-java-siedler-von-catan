package de.svenojo.catan.player;

import com.badlogic.gdx.graphics.Color;

public class Player {
    
    private Color color;
    private String name;

    private int score;

    private int streetAmount;
    private int settlementAmount;
    private int cityAmount;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;

        score = 0;
        streetAmount = 0;
        settlementAmount = 0;
        cityAmount = 0;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getStreetAmount() {
        return streetAmount;
    }

    public int getSettlementAmount() {
        return settlementAmount;
    }

    public int getCityAmount() {
        return cityAmount;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStreetAmount(int streetAmount) {
        this.streetAmount = streetAmount;
    }

    public void setSettlementAmount(int settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public void setCityAmount(int cityAmount) {
        this.cityAmount = cityAmount;
    }
}

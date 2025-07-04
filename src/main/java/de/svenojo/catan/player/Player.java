package de.svenojo.catan.player;

import com.badlogic.gdx.graphics.Color;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {
    private int id;
    
    private Color color;
    private String name;

    private int score;

    private int streetAmount;
    private int settlementAmount;
    private int cityAmount;

    public Player(int id, String name, Color color) {
        this.name = name;
        this.color = color;

        score = 0;
        streetAmount = 0;
        settlementAmount = 0;
        cityAmount = 0;
    }
}

package de.svenojo.catan.player;

import com.badlogic.gdx.graphics.Color;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {    
    private Color color;
    private String name;

    private int score;

    private int streetAmount;
    private int settlementAmount;
    private int cityAmount;

    private MaterialContainer materialContainer;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;

        score = 0;
        streetAmount = 0;
        settlementAmount = 0;
        cityAmount = 0;

        materialContainer = new MaterialContainer();
    }
}

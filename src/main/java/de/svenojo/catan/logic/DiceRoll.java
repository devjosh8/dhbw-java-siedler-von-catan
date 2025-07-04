package de.svenojo.catan.logic;

import java.util.Random;

public class DiceRoll {
    private Random random = new Random();

    private int singleDiceRoll() {
        return this.random.nextInt(5) + 1; 
    }
    public int rollBothDice() {
        int firstDice = singleDiceRoll();
        int secondDice = singleDiceRoll();

        return firstDice + secondDice;
    }
}

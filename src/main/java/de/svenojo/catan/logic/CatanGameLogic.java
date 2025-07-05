package de.svenojo.catan.logic;

import de.svenojo.catan.player.Player;

public class CatanGameLogic {
    enum GameState {
        PRE_GAME,
        SETTLE_PLAYERS,
        GAME,
        GAME_ENDING
    }

    enum RoundPhase {
        DICE_ROLL,
        MATERIAL_DISTRIBUTION,
        ROBBER,
        BUILD;

        public NextPhaseResult next() {
            int nextOrdinal = this.ordinal() + 1;
            RoundPhase[] phases = RoundPhase.values();

            if (nextOrdinal < phases.length) {
                return new NextPhaseResult(phases[nextOrdinal], false);
            } else {
                return new NextPhaseResult(phases[0], true);
            }
        }
    }

    private final DiceRoll diceRoll = new DiceRoll();

    private GameState currentGameState;
    private RoundPhase currentRoundPhase;

    private Player[] players;
    private int currentPlayerIndex;

    private int rolledNumber;

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public void nextPlayer() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % players.length;
    }

    public void nextRoundPhase() {
        NextPhaseResult result = this.currentRoundPhase.next();
        currentRoundPhase = result.getNextPhase();
        if (result.isFullRoundCompleted())
            nextPlayer();
    }

    public void playRoundPhase() {
        switch (currentRoundPhase) {
            case DICE_ROLL:
                this.rolledNumber = diceRoll.rollBothDice();
                break;
            case MATERIAL_DISTRIBUTION:
                if (this.rolledNumber == 7)
                    break;
                break;
            case ROBBER:
                if (this.rolledNumber != 7)
                    break;

                break;
            case BUILD:

                break;

            default:
                System.err.println("The current RoundPhase " + currentRoundPhase.toString() + " cannot be played.");
                break;
        }
    }
}

package de.svenojo.catan.logic;

import de.svenojo.catan.player.Player;

public class CatanGameLogic {
    enum GameState {
        PRE_GAME,
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

    private GameState currentGameState;
    private RoundPhase currentRoundPhase;
    private Player[] players;
    private int currentPlayerIndex;

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public void nextPlayer() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % players.length;
    }

    public void nextRoundPhase() {
        NextPhaseResult result = this.currentRoundPhase.next();
        currentRoundPhase = result.getNextPhase();
        if (result.isFullRoundCompleted()) nextPlayer();
    }
}

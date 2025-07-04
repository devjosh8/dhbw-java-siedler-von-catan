package de.svenojo.catan.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class NextPhaseResult {
    private final CatanGameLogic.RoundPhase nextPhase;
    private final boolean fullRoundCompleted;
}

package de.svenojo.catan.logic.events;

import de.svenojo.catan.world.material.MaterialType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TryToConfirmTradeEvent {
    public int amountToGive;
    public MaterialType typeToGive;
    public MaterialType typeToReceive;
}

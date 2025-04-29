package de.svenojo.catan.world.building.buildings;

import de.svenojo.catan.player.Player;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.building.Building;
import de.svenojo.catan.world.building.BuildingType;

public class BuildingStreet extends Building {

    public Edge position;

    public BuildingStreet(Player player, Edge position) {
        super(player, BuildingType.STREET);
        this.position = position;
    }

    public Edge getPosition() {
        return position;
    }
}

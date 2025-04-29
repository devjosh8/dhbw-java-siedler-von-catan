package de.svenojo.catan.world.building.buildings;

import de.svenojo.catan.player.Player;
import de.svenojo.catan.world.Node;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.building.NodeBuilding;

public class BuildingCity extends NodeBuilding {

    public BuildingCity(Player player, Node position) {
        super(player, BuildingType.CITY, position);
    }
    
}

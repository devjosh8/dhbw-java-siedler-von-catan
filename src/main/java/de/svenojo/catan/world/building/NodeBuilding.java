package de.svenojo.catan.world.building;

import de.svenojo.catan.player.Player;
import de.svenojo.catan.world.Node;

public class NodeBuilding extends Building {

    private Node position;

    public NodeBuilding(Player player, BuildingType buildingType, Node position) {
        super(player, buildingType);
        this.position = position;
    }

    public Node getPosition() {
        return position;
    }
    
}

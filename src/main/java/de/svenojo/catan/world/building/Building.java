package de.svenojo.catan.world.building;

import de.svenojo.catan.player.Player;

public class Building {
    
    private Player player;
    private BuildingType buildingType;

    public Building(Player player, BuildingType buildingType) {
        this.player = player;
        this.buildingType = buildingType;
    }

    public Player getPlayer() {
        return player;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }
}

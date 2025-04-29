package de.svenojo.catan.world;

import java.util.Optional;

import de.svenojo.catan.world.building.buildings.BuildingStreet;

public class Edge {
    
    private Optional<BuildingStreet> street;

    public void setStreet(BuildingStreet street) {
        this.street = Optional.of(street);
    }

    public boolean hasStreet() {
        return street.isPresent();
    }

    public BuildingStreet getStreet() {
        if(!street.isPresent()) return null;
        return street.get();
    }
}

package de.svenojo.catan.world.util;

import de.svenojo.catan.world.building.BuildingType;

public enum HighlightingType {
    
    NONE,
    TILE,
    NODE,
    EDGE,
    HARBOUR;

    public static HighlightingType fromBuildingType(BuildingType buildingType) {
        return switch (buildingType) {
            case SETTLEMENT -> NODE;
            case CITY -> NODE;
            case STREET -> EDGE;
            default -> NONE;
        };
    }

}

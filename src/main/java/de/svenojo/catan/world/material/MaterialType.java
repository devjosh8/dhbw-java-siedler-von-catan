package de.svenojo.catan.world.material;

public enum MaterialType {
    
    WOOD,
    ORE,
    WHEAT,
    WOOL,
    CLAY,
    NONE;

    public static MaterialType[] actualMaterialValues() {
        return new MaterialType[] { WOOD, ORE, WHEAT, WOOL, CLAY };
    }
}

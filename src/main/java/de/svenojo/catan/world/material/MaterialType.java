package de.svenojo.catan.world.material;

public enum MaterialType {
    
    WOOD("data/images/material/wood.png"),
    ORE("data/images/material/ore.png"),
    WHEAT("data/images/material/wheat.png"),
    WOOL("data/images/material/wool.png"),
    CLAY("data/images/material/clay.png"),
    NONE("data/images/material/none.png");

    private String fileName;

    private MaterialType(String fileName) {
        this.fileName = fileName;
    }

    public static MaterialType[] actualMaterialValues() {
        return new MaterialType[] { WOOD, ORE, WHEAT, WOOL, CLAY };
    }

    public String getFileName() {
        return fileName;
    }
}

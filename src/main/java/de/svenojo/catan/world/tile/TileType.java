package de.svenojo.catan.world.tile;

public enum TileType {
    
    WOODS("data/models/karte_wald.g3db"),
    DESERT("data/models/karte_wueste.g3db"),
    MOUNTAINS("data/models/karte_berge.g3db");


    private String fileName;
    TileType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

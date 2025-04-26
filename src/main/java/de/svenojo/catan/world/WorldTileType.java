package de.svenojo.catan.world;

public enum WorldTileType {
    
    WOODS("data/models/karte_wald.g3db"),
    DESERT("data/models/karte_wueste.g3db"),
    MOUNTAINS("data/models/karte_berge.g3db");


    private String fileName;
    WorldTileType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

package de.svenojo.catan.world;

public enum WorldTileType {
    
    WOODS("data/models/wald.g3db");


    private String fileName;
    WorldTileType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

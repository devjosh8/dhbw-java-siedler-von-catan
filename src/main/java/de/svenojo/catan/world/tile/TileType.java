package de.svenojo.catan.world.tile;

import de.svenojo.catan.world.material.MaterialType;

public enum TileType {

    /** Für Holz */
    FOREST("data/models/karte_wald.g3db", MaterialType.WOOD),
    /** Gibt nix */
    DESERT("data/models/karte_wueste.g3db", MaterialType.NONE),
    /** Für Erz */
    MOUNTAINS("data/models/karte_berge.g3db", MaterialType.ORE),
    /** Für Weizen */
    FIELDS("data/models/karte_farmland.g3db", MaterialType.WHEAT),
    /** für Schaf / Wolle */
    PASTURE("data/models/karte_weideland.g3db", MaterialType.WOOL),
    /** Für Lehm */
    HILLS("data/models/karte_lehm.g3db", MaterialType.CLAY);

    private String fileName;
    private MaterialType materialType;

    TileType(String fileName, MaterialType materialType) {
        this.fileName = fileName;
        this.materialType = materialType;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public String getFileName() {
        return fileName;
    }

    
}

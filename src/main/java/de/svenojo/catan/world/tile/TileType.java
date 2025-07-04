package de.svenojo.catan.world.tile;

public enum TileType {

    /** Für Holz */
    FOREST("data/models/karte_wald.g3db"),
    /** Gibt nix */
    DESERT("data/models/karte_wueste.g3db"),
    /** Für Erz */
    MOUNTAINS("data/models/karte_berge.g3db"),
    /** Für Weizen */
    FIELDS("data/models/karte_farmland.g3db"),
    /** für Schaf / Wolle */
    PASTURE("data/models/karte_weideland.g3db"),
    /**
     * TODO: richtiges modell einfügen für Lehm
     * 
     * Für Lehm
     */
    HILLS("data/models/karte_wueste.g3db");

    private String fileName;

    TileType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

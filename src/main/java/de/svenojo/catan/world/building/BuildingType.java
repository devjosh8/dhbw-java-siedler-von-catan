package de.svenojo.catan.world.building;

public enum BuildingType {
    
    STREET("data/models/buildings/street.g3db"),
    SETTLEMENT("data/models/buildings/settlement.g3db"),
    CITY("data/models/buildings/city.g3db"),
    HARBOUR("data/models/buildings/harbour.g3db");

    String fileName;
    BuildingType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

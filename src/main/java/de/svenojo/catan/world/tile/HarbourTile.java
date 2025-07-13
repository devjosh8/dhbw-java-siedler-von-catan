package de.svenojo.catan.world.tile;

import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.world.material.MaterialType;

public class HarbourTile extends Tile {

    private MaterialType materialType;

    public HarbourTile(AxialVector position, int numberValue, MaterialType materialType) {
        super(position, TileType.HARBOUR, numberValue);
        this.materialType = materialType;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }
    
}

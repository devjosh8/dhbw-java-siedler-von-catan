package de.svenojo.catan.world.tile;

import java.util.Random;

import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.math.AxialVector;

public class Tile {
    
    public static final float WORLD_TILE_SCALE = 0.02f;

    // bestimmt den Abstand zwischen zwei Hexzellen; 2.0f entspricht keinem Abstand
    public static final float WORLD_TILE_DISTANCE = 2.0f;

    // Koordinaten als axiale Koordinaten (Q verläuft vertikal, r verläuft diagonal)
    private AxialVector axialPosition;
    private Vector3 worldPosition;
    private TileType worldTileType;

    private int numberValue;

    public Tile(AxialVector position, TileType type) {
        this.axialPosition = position;
        this.worldTileType = type;
        this.worldPosition = new Vector3();
        numberValue = new Random().nextInt(10);
        calculateWorldPosition();
    }

    private void calculateWorldPosition() {
        float size = WORLD_TILE_DISTANCE;
    
        float x = size * (float) Math.sqrt(3) * (axialPosition.getQ() + axialPosition.getR() / 2.0f);
        float z = size * 1.5f * axialPosition.getR();
    
        worldPosition.set(x, 0, z);
    }

    public Vector3 getWorldPosition() {
        return worldPosition;
    }

    public TileType getWorldTileType() {
        return worldTileType;
    }

    public AxialVector getAxialPosition() {
        return axialPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Tile)) return false;
        Tile worldTile = (Tile) obj;
        return worldTile.axialPosition.equals(axialPosition);
    }

    public int getNumberValue() {
        return numberValue;
    }
}

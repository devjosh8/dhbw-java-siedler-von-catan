package de.svenojo.catan.world;

import com.badlogic.gdx.math.Vector2;

import de.svenojo.catan.math.AxialVector;

public class WorldTile {
    
    public static final float WORLD_TILE_SCALE = 0.02f;

    // bestimmt den Abstand zwischen zwei Hexzellen; 2.0f entspricht keinem Abstand
    public static final float WORLD_TILE_DISTANCE = 2.1f;

    // Koordinaten als axiale Koordinaten (Q verläuft vertikal, r verläuft diagonal)
    private AxialVector axialPosition;
    private Vector2 worldPosition;
    private WorldTileType worldTileType;

    public WorldTile(AxialVector position, WorldTileType type) {
        this.axialPosition = position;
        this.worldTileType = type;
        this.worldPosition = new Vector2();
        calculateWorldPosition();
    }

    private void calculateWorldPosition() {
        float size = WORLD_TILE_DISTANCE;
    
        float x = size * (float) Math.sqrt(3) * (axialPosition.getQ() + axialPosition.getR() / 2.0f);
        float y = size * 1.5f * axialPosition.getR();
    
        worldPosition.set(x, y);
    }

    public Vector2 getWorldPosition() {
        return worldPosition;
    }

    public WorldTileType getWorldTileType() {
        return worldTileType;
    }

    public AxialVector getAxialPosition() {
        return axialPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof WorldTile)) return false;
        WorldTile worldTile = (WorldTile) obj;
        return worldTile.axialPosition.equals(axialPosition);
    }
}

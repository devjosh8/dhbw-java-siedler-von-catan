package de.svenojo.catan.world;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.world.tile.Tile;

public class Node {
    
    private Vector3 position;
    private Set<Tile> neighbourTiles;

    @Deprecated
    private int number;

    private boolean isOnEdge;
    private boolean hasHarbour;
    
    public Node(Vector3 position) {
        this.position = position;
        neighbourTiles = new HashSet<>();
        this.number = 0;
        isOnEdge = false;
        hasHarbour = false;
    }

    public void setHasHarbour(boolean hasHarbour) {
        this.hasHarbour = hasHarbour;
    }

    public boolean hasHarbour() {
        return hasHarbour;
    }


    public boolean isOnEdge() {
        return isOnEdge;
    }

    public void setOnEdge(boolean isOnEdge) {
        this.isOnEdge = isOnEdge;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Set<Tile> getNeighbourTiles() {
        return neighbourTiles;
    }

    public void addNeighbourTile(Tile tile) {
        neighbourTiles.add(tile);
    }
    
    public int getNumber() {
        return number;
    }
}

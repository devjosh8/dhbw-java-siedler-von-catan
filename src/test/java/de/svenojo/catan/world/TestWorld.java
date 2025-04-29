package de.svenojo.catan.world;

import org.junit.jupiter.api.Test;

import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.resources.CatanAssetManager;

import static org.junit.jupiter.api.Assertions.*;

import org.jgrapht.Graph;

public class TestWorld {
    
    @Test
    /**
     * Test, um Axialvektoren zu testen
     */
    public void testAxialVectors() {
        AxialVector a = new AxialVector(1, 1);
        AxialVector b = new AxialVector(1, 1);
        AxialVector c = new AxialVector(1, 2);

        assertTrue(a.equals(b));
        assertFalse(a.equals(c));
        assertFalse(b.equals(c));
        assertTrue(c.equals(c));
    }

    @Test
    /**
     * Testet, dass der generierte Graph die richtigen Eigenschaften
     * für das Spiel aufweist
     */
    public void testMapGenerationNodeGraph() {
        WorldMap map = new WorldMap(new CatanAssetManager());
        map.generateMap();

        Graph<Node, Edge> nodeGraph = map.getNodeGraph();
        

        for(Node n : nodeGraph.vertexSet()) {
            assertTrue(nodeGraph.edgesOf(n).size() <= 3);
        }

        for(Node n : nodeGraph.vertexSet()) {
            assertTrue(n.getNeighbourTiles().size() <= 3);
        }
    }
}

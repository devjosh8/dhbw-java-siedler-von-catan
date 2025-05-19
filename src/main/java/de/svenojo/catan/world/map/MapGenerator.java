package de.svenojo.catan.world.map;

import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;

import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.Node;
import de.svenojo.catan.world.tile.Tile;
import de.svenojo.catan.world.tile.TileType;

public class MapGenerator {

    /**
     * Generiert die Karte für das Spiel, dazu den NodeGraph und befüllt alle Argumente mit Inhalt
     * @param mapTiles eine leere Liste, die mit den generierten MapTiles gefüllt wird
     * @param nodeGraph eine Leere Graph<Node, Edge> Einheit, die mit dem Graphen der Karte bestehend aus Nodes
     * und Edges gefüllt wird
     * @param nodes Eine Leere Liste von Nodes, die durch die Methide gefüllt wird
     */
    public static void generateMap(List<Tile> mapTiles, Graph<Node, Edge> nodeGraph, List<Node> nodes) {
        int mapRadius = 2;

        for (int q = -mapRadius; q <= mapRadius; q++) {
            for (int r = Math.max(-mapRadius, -q - mapRadius); r <= Math.min(mapRadius, -q + mapRadius); r++) {
                AxialVector tilePosition = new AxialVector(q, r);
                Tile worldTile = new Tile(tilePosition, TileType.values()[new Random().nextInt(TileType.values().length)]);
                mapTiles.add(worldTile);
                
            }
        }

        generateNodeGraph(mapTiles, nodeGraph, nodes);
    }

    private static void generateNodeGraph(List<Tile> mapTiles, Graph<Node, Edge> nodeGraph, List<Node> nodes) {
        Vector3[] positions = {
            new Vector3(0, 0, -2),
            new Vector3((float) Math.sqrt(3), 0, -1),
            new Vector3((float) Math.sqrt(3), 0, 1),
            new Vector3(0, 0, 2),
            new Vector3((float) -Math.sqrt(3), 0, 1),
            new Vector3((float) -Math.sqrt(3), 0, -1),

            new Vector3(0, 0, -2)
        };


        float nodePositionTolerance = 0.1f;
        Node lastNode = null;
        boolean createNewNode = false;
        int nodesCreatedCounter = 0;

        for(Tile tile : mapTiles) {
            float tileX = tile.getWorldPosition().x;
            float tileZ = tile.getWorldPosition().z;

            for(Vector3 offsetPosition : positions) {
                createNewNode = true;
                for(Node node : nodes) {
                    if(     Math.abs(node.getPosition().x - (tileX + offsetPosition.x)) <= nodePositionTolerance 
                        &&  Math.abs(node.getPosition().z - (tileZ + offsetPosition.z) )<= nodePositionTolerance ) {
                        createNewNode = false;

                        node.addNeighbourTile(tile);
                        if(lastNode != null) {
                            if(! (nodeGraph.containsEdge(node, lastNode)) ) {
                                nodeGraph.addEdge(node, lastNode);
                                lastNode = node;
                                break;
                            }
                        }
                        lastNode = node;
                    }
                }

                
                if(createNewNode) {
                    nodesCreatedCounter++;
                    Node currentNode = new Node(new Vector3(tileX + offsetPosition.x, 0, tileZ + offsetPosition.z));
                    currentNode.setNumber(nodesCreatedCounter);
                    currentNode.addNeighbourTile(tile);

                    nodes.add(currentNode);
                    nodeGraph.addVertex(currentNode);

                    if(lastNode != null) {
                        nodeGraph.addEdge(currentNode, lastNode);
                    }

                    lastNode = currentNode;
                }
            } 
            lastNode = null;
        }
    }
}

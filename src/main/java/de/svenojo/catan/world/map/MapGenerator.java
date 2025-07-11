package de.svenojo.catan.world.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.Node;
import de.svenojo.catan.world.WorldMap;
import de.svenojo.catan.world.building.buildings.BuildingHarbour;
import de.svenojo.catan.world.tile.Tile;
import de.svenojo.catan.world.tile.TileType;

public class MapGenerator {

    /**
     * Generiert einen gemischten Stack mit der richigen Anzahl an verschiedenen
     * Tile Types.
     * 
     * @return gemischter Stack an Tile Types
     */
    public static Stack<TileType> getTileTypeDistributionStack() {
        Stack<TileType> tileTypeStack = new Stack<>();
        for (int i = 0; i < 4; i++) {
            tileTypeStack.push(TileType.FOREST);
            tileTypeStack.push(TileType.FIELDS);
            tileTypeStack.push(TileType.PASTURE);
        }
        for (int i = 0; i < 3; i++) {
            tileTypeStack.push(TileType.MOUNTAINS);
            tileTypeStack.push(TileType.HILLS);
        }
        tileTypeStack.push(TileType.DESERT);

        Collections.shuffle(tileTypeStack);

        return tileTypeStack;
    }

    /**
     * Generiert einen gemischten Stack mit der richtigen Anzahl an Nummern für die
     * einzelnen Felder
     * 
     * @return gemischter Stack an Nummern für die Felder (Tiles)
     */

    public static Stack<Integer> getTileNumberDistributionStack() {
        Stack<Integer> tileNumberStack = new Stack<>();
        int[] doubleNumbers = { 3, 4, 5, 6, 8, 9, 10, 11 };
        int[] singleNumbers = { 2, 12 };

        for (int i : doubleNumbers) {
            tileNumberStack.push(i);
            tileNumberStack.push(i);
        }

        for (int i : singleNumbers) {
            tileNumberStack.push(i);
        }

        Collections.shuffle(tileNumberStack);
        return tileNumberStack;
    }

    /**
     * Generiert die Karte für das Spiel, dazu den NodeGraph und befüllt alle
     * Argumente mit Inhalt
     * 
     * @param mapTiles  eine leere Liste, die mit den generierten MapTiles gefüllt
     *                  wird
     * @param nodeGraph eine Leere Graph<Node, Edge> Einheit, die mit dem Graphen
     *                  der Karte bestehend aus Nodes
     *                  und Edges gefüllt wird
     * @param nodes     Eine Leere Liste von Nodes, die durch die Methide gefüllt
     *                  wird
     */
    public static void generateMap(List<Tile> mapTiles, Graph<Node, Edge> nodeGraph, List<Node> nodes, WorldMap worldMap) {
        int mapRadius = 2;

        Stack<TileType> tileTypeStack = getTileTypeDistributionStack();
        Stack<Integer> tileNumberStack = getTileNumberDistributionStack();

        for (int q = -mapRadius; q <= mapRadius; q++) {
            for (int r = Math.max(-mapRadius, -q - mapRadius); r <= Math.min(mapRadius, -q + mapRadius); r++) {
                AxialVector tilePosition = new AxialVector(q, r);

                TileType tileType = tileTypeStack.pop();
                int numberValue = (tileType != TileType.DESERT) ? tileNumberStack.pop() : 7;
                Tile worldTile = new Tile(tilePosition, tileType, numberValue);
                mapTiles.add(worldTile);

            }
        }

        generateNodeGraph(mapTiles, nodeGraph, nodes, worldMap);
    }

    private static void generateNodeGraph(List<Tile> mapTiles, Graph<Node, Edge> nodeGraph, List<Node> nodes, WorldMap worldMap) {
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

        for (Tile tile : mapTiles) {
            float tileX = tile.getWorldPosition().x;
            float tileZ = tile.getWorldPosition().z;

            for (Vector3 offsetPosition : positions) {
                createNewNode = true;
                for (Node node : nodes) {
                    if (Math.abs(node.getPosition().x - (tileX + offsetPosition.x)) <= nodePositionTolerance
                            && Math.abs(node.getPosition().z - (tileZ + offsetPosition.z)) <= nodePositionTolerance) {
                        createNewNode = false;

                        node.addNeighbourTile(tile);
                        if (lastNode != null) {
                            if (!(nodeGraph.containsEdge(node, lastNode))) {
                                nodeGraph.addEdge(node, lastNode, new Edge(node, lastNode));
                                lastNode = node;
                                break;
                            }
                        }
                        lastNode = node;
                    }
                }

                if (createNewNode) {
                    nodesCreatedCounter++;
                    Node currentNode = new Node(new Vector3(tileX + offsetPosition.x, 0, tileZ + offsetPosition.z));
                    currentNode.setNumber(nodesCreatedCounter);
                    currentNode.addNeighbourTile(tile);

                    nodes.add(currentNode);
                    nodeGraph.addVertex(currentNode);

                    if (lastNode != null) {
                        nodeGraph.addEdge(currentNode, lastNode, new Edge(lastNode, currentNode));
                    }

                    lastNode = currentNode;
                }
            }
            lastNode = null;
        }

        for(Node node : nodes) {
            if(nodeGraph.edgesOf(node).size() == 2) {
                node.setOnEdge(true);
                //worldMap.placeHarbour(new BuildingHarbour(null, node));
            }
        }
    }

    public static void placeHarbours(Graph<Node, Edge> graph, Node start, WorldMap worldMap) {


    for(Node node : graph.vertexSet()) {
        if(!node.isOnEdge()) continue;

        boolean placeHarbour = true;

        for(Edge edge : graph.edgesOf(node)) {
            Node neighbor = Graphs.getOppositeVertex(graph, edge, node);
            if(!neighbor.isOnEdge()) continue;

            // hat nachbar hafen
            if(neighbor.hasHarbour()) {
                placeHarbour = false;
                break;
            }
        }

        if(placeHarbour)worldMap.placeHarbour(new BuildingHarbour(null, node));
    }
}
}

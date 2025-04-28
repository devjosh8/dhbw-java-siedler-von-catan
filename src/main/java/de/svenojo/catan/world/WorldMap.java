package de.svenojo.catan.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.resources.CatanAssetManager;

public class WorldMap implements IRenderable, ITickable {
    
    private List<Tile> mapTiles;
    private List<ModelInstance> modelInstances;
    private List<Node> nodes;

    private Graph<Node, DefaultEdge> nodeGraph;


    private CatanAssetManager catanAssetManager;

    public WorldMap(CatanAssetManager catanAssetManager) {
        mapTiles = new ArrayList<>();
        modelInstances = new ArrayList<>();
        nodes = new ArrayList<>();
        this.catanAssetManager = catanAssetManager;

        nodeGraph = GraphTypeBuilder
            .<Node, DefaultEdge> undirected().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
    }

    /**
     * Generiert ein regelmäßiges Sechseck aus Tiles
     * setzt die kartesische Position bei Tiles aus der berechneten axialen Position
     */
    public void generateMap() {
        int mapRadius = 2;

        for (int q = -mapRadius; q <= mapRadius; q++) {
            for (int r = Math.max(-mapRadius, -q - mapRadius); r <= Math.min(mapRadius, -q + mapRadius); r++) {
                AxialVector tilePosition = new AxialVector(q, r);
                Tile worldTile = new Tile(tilePosition, TileType.values()[new Random().nextInt(TileType.values().length)]);
                mapTiles.add(worldTile);
                
            }
        }

        generateNodeGraph();
    }

    /**
     * Generiert den ungerichteten, ungewichteten Graph für die Nodes, die sich an den
     * Ecken von den Tiles (Sechseckigen Kartenteilen) befinden
     */
    private void generateNodeGraph() {
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

    public void loadModels() {
        for(Tile worldTile : mapTiles) {
           Model worldTileModel = catanAssetManager.getAssetManager().get(worldTile.getWorldTileType().getFileName(), Model.class);
           ModelInstance modelInstance = new ModelInstance(worldTileModel);

           modelInstance.transform.setToTranslation(worldTile.getWorldPosition().x, 0, worldTile.getWorldPosition().z);
           modelInstance.transform.scale(Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE);
           modelInstances.add(modelInstance);
        }
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstances, environment);
    }

    @Override
    public void tick(float delta) { }
}

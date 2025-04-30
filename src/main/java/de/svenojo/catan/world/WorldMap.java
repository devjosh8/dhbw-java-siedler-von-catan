package de.svenojo.catan.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.IRenderable2D;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.building.Building;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.building.NodeBuilding;
import de.svenojo.catan.world.building.buildings.BuildingStreet;

public class WorldMap implements IRenderable, IRenderable2D, ITickable {
    
    private List<Tile> mapTiles;
    private Set<ModelInstance> modelInstances;
    private List<Node> nodes;
    private Set<Building> buildings;

    private Graph<Node, Edge> nodeGraph;
    private CatanAssetManager catanAssetManager;

    private BitmapFont bitmapFont;

    public WorldMap(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
        this.bitmapFont = null;
        mapTiles = new ArrayList<>();
        modelInstances = new HashSet<>();
        nodes = new ArrayList<>();
        buildings = new HashSet<>();

        nodeGraph = GraphTypeBuilder
            .<Node, Edge> undirected().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(Edge.class).weighted(false).buildGraph();
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

    public void loadAssets() {
        for(Tile worldTile : mapTiles) {
           Model worldTileModel = catanAssetManager.getAssetManager().get(worldTile.getWorldTileType().getFileName(), Model.class);
           ModelInstance modelInstance = new ModelInstance(worldTileModel);

           modelInstance.transform.setToTranslation(worldTile.getWorldPosition().x, 0, worldTile.getWorldPosition().z);
           modelInstance.transform.scale(Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE);
           modelInstances.add(modelInstance);
        }

        bitmapFont = catanAssetManager.worldMapIslandNumberFont;
    }

    public void placeBuilding(Player player, Building building) {
        if(building.getBuildingType() == BuildingType.STREET && building instanceof BuildingStreet) {
            buildings.add(building);

            ModelBuilder modelBuilder = new ModelBuilder();

            // TODO: Platzhaltercode für Straße durch eigentliches Modell ersetzen
            Model boxModel = modelBuilder.createBox(
                1f, 1f, 5f,                                     // width, height, depth
                new Material(ColorAttribute.createDiffuse(player.getColor())), // Material (z. B. blau)
                Usage.Position | Usage.Normal                   // Vertex-Attribute
            );
            ModelInstance instance = new ModelInstance(boxModel);
            

            BuildingStreet buildingStreet = (BuildingStreet) building;

            Vector3 position = new Vector3();
            Node source = nodeGraph.getEdgeSource(buildingStreet.getPosition());
            Node target = nodeGraph.getEdgeTarget(buildingStreet.getPosition());
            position.y = 0.1f;

            float delta_x = target.getPosition().x - source.getPosition().x;
            float delta_z = target.getPosition().z - source.getPosition().z;
            
            double theta = Math.tan((double) delta_x / delta_z) * 10.0d; //??? Warum mal 10?? funktioniert aber ._. wtf

            position.x = source.getPosition().x + (delta_x) / 2;
            position.z = source.getPosition().z + (delta_z) / 2;
            instance.transform.setToTranslation(position);
            instance.transform.rotate(new Vector3(0, 1.0f, 0f), (float) (-theta));
            instance.transform.scale(0.3f, 0.3f, 0.3f);
            modelInstances.add(instance);
        } else if(building instanceof NodeBuilding) {
            // TODO: hier Settlement oder City platzieren
        }
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstances, environment);
    }

    @Override
    public void render2D(SpriteBatch spriteBatch, Environment environment, Camera camera) {
        for(Tile t : mapTiles) {
            Vector3 textPosition = new Vector3(t.getWorldPosition().x, 1.5f, t.getWorldPosition().z);
            Vector3 screenCoords = new Vector3(textPosition);
            camera.project(screenCoords);

            GlyphLayout layout = new GlyphLayout();
            layout.setText(bitmapFont, String.valueOf(t.getNumberValue()));

            if (screenCoords.z > 0 && screenCoords.z < 1) {
                bitmapFont.draw(spriteBatch, String.valueOf(t.getNumberValue()), screenCoords.x - layout.width / 2, screenCoords.y + layout.height / 2);
            }
        }
    }

    @Override
    public void tick(float delta) { }

    public Graph<Node, Edge> getNodeGraph() {
        return nodeGraph;
    }

    public Set<Building> getBuildings() {
        return buildings;
    }
}

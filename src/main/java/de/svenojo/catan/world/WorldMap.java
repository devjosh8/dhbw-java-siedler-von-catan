package de.svenojo.catan.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.IRenderable2D;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.bandit.Bandit;
import de.svenojo.catan.world.building.Building;
import de.svenojo.catan.world.building.BuildingCalculator;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.building.NodeBuilding;
import de.svenojo.catan.world.building.buildings.BuildingHarbour;
import de.svenojo.catan.world.building.buildings.BuildingStreet;
import de.svenojo.catan.world.map.MapGenerator;
import de.svenojo.catan.world.tile.HarbourTile;
import de.svenojo.catan.world.tile.Tile;
import de.svenojo.catan.world.tile.TileHighlighter;
import de.svenojo.catan.world.tile.TileMesh;
import de.svenojo.catan.world.tile.TileType;
import de.svenojo.catan.world.util.HighlightingType;
import lombok.Getter;

public class WorldMap implements IRenderable, IRenderable2D, ITickable {

    /*
     * Alle wichtigen Datenstrukturen für die Karte
     */
    private List<Tile> mapTiles;
    private CopyOnWriteArrayList<ModelInstance> modelInstances;
    private ArrayList<ModelInstance> harbourModelInstances;
    private ArrayList<TileMesh> tileMeshes;
    private ArrayList<TileMesh> harbourTileMeshes;

    private List<HarbourTile> harbourTiles;

    private List<Node> nodes;
    private Set<Building> buildings;
    private Graph<Node, Edge> nodeGraph;

    private CatanAssetManager catanAssetManager;

    private BitmapFont bitmapFont;

    private BuildingCalculator buildingCalculator;
    @Getter
    private Bandit bandit;

    /**
     * für die Kollisionsberechnung benötigte Hilfsvariablen
     */

    // Diese Variable kann verwendet werden, um zu setzen, welche Art von
    // Kollisionen abgefangen werden soll
    // Ist diese Variable auf NONE gesetzt, werden keine Kollisionen berechnet und
    // es kommen auch keine an! (default ist NONE!)
    // Für Annahme der Kollisionen die Getter unten verwenden
    private HighlightingType highlightingType;

    private Tile currentlyHighlightedTile = null;

    private Node currentlyHighlightedNode;
    private ModelInstance highlightedNodeModelInstance;

    private Edge currentlyHighlightedEdge;
    private ModelInstance highlightedEdgeModelInstance;

    private HarbourTile currentlyHighlightedHarbourTile;

    public WorldMap(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
        this.bitmapFont = null;
        mapTiles = new ArrayList<>();
        modelInstances = new CopyOnWriteArrayList<>();
        tileMeshes = new ArrayList<>();
        harbourModelInstances = new ArrayList<>();
        nodes = new ArrayList<>();
        harbourTileMeshes = new ArrayList<>();
        buildings = Collections.newSetFromMap(new ConcurrentHashMap<>());;
        harbourTiles = new ArrayList<>();
        this.buildingCalculator = new BuildingCalculator(catanAssetManager);
        bandit = new Bandit(catanAssetManager);

        nodeGraph = GraphTypeBuilder
                .<Node, Edge>undirected().allowingMultipleEdges(false)
                .allowingSelfLoops(false).edgeClass(Edge.class).weighted(false).buildGraph();

        highlightingType = HighlightingType.NONE;
    }

    /**
     * Generiert die Karte über die MapGenerator Klasse
     */
    public void generateMap() {
        MapGenerator.generateMap(mapTiles, nodeGraph, nodes, this, harbourTiles);

        for (Node node : nodeGraph.vertexSet()) {
            if (node.isOnEdge()) {
                // MapGenerator.placeHarbours(nodeGraph, node, this);
                break;
            }
        }
    }

    public void loadAssets() {
        for (Tile worldTile : mapTiles) {
            Model worldTileModel = catanAssetManager.getAssetManager().get(worldTile.getWorldTileType().getFileName(),
                    Model.class);
            ModelInstance modelInstance = new ModelInstance(worldTileModel);

            modelInstance.transform.setToTranslation(worldTile.getWorldPosition().x, 0, worldTile.getWorldPosition().z);
            modelInstance.transform.scale(Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE);

            // Specular Map neu machen, damit die Map nicht mehr so glänzt
            Material material = modelInstance.materials.get(0);
            material.remove(ColorAttribute.Specular);
            material.set(ColorAttribute.createSpecular(Color.BLACK));
            material.set(FloatAttribute.createShininess(2f));

            modelInstances.add(modelInstance);

            TileMesh tileMesh = new TileMesh();
            tileMesh.setHexagonTriangles(worldTile.getWorldPosition(), Tile.WORLD_TILE_DISTANCE);
            tileMeshes.add(tileMesh);

            if (worldTile.getWorldTileType() == TileType.DESERT) {
                placeBandit(worldTile);
            }
        }
        bitmapFont = catanAssetManager.worldMapIslandNumberFont;

        float[] harbourRotations = new float[] {
                -60.0f,
                0.0f,
                -120.0f,
                -180.0f,
                -240.0f,
                -300.0f,
        };
        int i = 0;
        for (Tile harbour : harbourTiles) {
            Model worldTileModel = catanAssetManager.getAssetManager().get(harbour.getWorldTileType().getFileName(),
                    Model.class);
            ModelInstance modelInstance = new ModelInstance(worldTileModel);

            modelInstance.transform.setToTranslation(harbour.getWorldPosition().x, 0, harbour.getWorldPosition().z);
            modelInstance.transform.rotate(new Vector3(0, 1.0f, 0f), harbourRotations[i]);
            modelInstance.transform.scale(Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE, Tile.WORLD_TILE_SCALE);

            // Specular Map neu machen, damit die Map nicht mehr so glänzt
            Material material = modelInstance.materials.get(0);
            material.remove(ColorAttribute.Specular);
            material.set(ColorAttribute.createSpecular(Color.BLACK));
            material.set(FloatAttribute.createShininess(2f));

            harbourModelInstances.add(modelInstance);

            TileMesh tileMesh = new TileMesh();
            tileMesh.setHexagonTriangles(harbour.getWorldPosition(), Tile.WORLD_TILE_DISTANCE);
            harbourTileMeshes.add(tileMesh);

            i++;
        }
    }

    public void placeBuilding(Player player, Building building) {
        ModelInstance modelInstance = buildingCalculator.calculateBuildingModelInstance(player, building, nodeGraph);
        if (modelInstance == null) {
            Gdx.app.log("WorldMap", "Building could not be placed: " + building);
            return;
        }
        switch (building.getBuildingType()) {
            case SETTLEMENT:
                player.setSettlementAmount(player.getSettlementAmount() + 1);
                break;
            case CITY:
                player.setCityAmount(player.getCityAmount() + 1);
                break;
            case STREET:
                player.setStreetAmount(player.getStreetAmount() + 1);
                break;
            default:
                break;
        }

        buildings.add(building);
        modelInstances.add(modelInstance);

    }

    public void removeBuilding(Player player, Building building) {
        if (!buildings.contains(building)) {
            Gdx.app.log("WorldMap", "Building not found: " + building);
            return;
        }
        synchronized (this) { // Synchronize if both collections are modified together
            buildings.remove(building);

            // Find and remove the corresponding model instance
            boolean wasRemoved = modelInstances.removeIf(modelInstances -> modelInstances.userData == building);
            Gdx.app.log("WorldMap", "Building removed: " + building + ", was model instance removed: " + wasRemoved);
        }

        switch (building.getBuildingType()) {
            case SETTLEMENT:
                player.setSettlementAmount(player.getSettlementAmount() + 1);
                break;
            case CITY:
                player.setCityAmount(player.getCityAmount() + 1);
                break;
            case STREET:
                player.setStreetAmount(player.getStreetAmount() + 1);
                break;
            default:
                break;
        }
    }

    public void placeHarbour(BuildingHarbour harbour) {
        ModelInstance modelInstance = buildingCalculator.calculateHarbourModelInstance(harbour, nodeGraph);
        if (modelInstance != null) {
            buildings.add(harbour);
            modelInstances.add(modelInstance);
        }
    }

    /**
     * Methode, die den Bandit auf der Karte platziert
     * 
     * @param tile Das Tile auf dem der Bandit platziert werden soll
     */
    public void placeBandit(Tile tile) {
        if (tile.getWorldTileType() == TileType.HARBOUR)
            return;
        bandit.setPosition(tile);
    }

    private void highlightObjectUnderMouse(ModelBatch modelBatch, Environment environment) {

        Ray ray = modelBatch.getCamera().getPickRay(Gdx.input.getX(), Gdx.input.getY());

        boolean raycastHit = false;

        switch (getHighlightingType()) {
            case NONE:
                return;
            case TILE: {
                int raycastHitMeshIndex = 0;
                for (TileMesh currentTileMesh : tileMeshes) {

                    Vector3 hitpoint = Vector3.Zero;
                    if (currentTileMesh.rayIntersectsHex(ray, hitpoint)) {
                        raycastHit = true;
                        break;
                    }
                    raycastHitMeshIndex++;
                }

                if (raycastHit) {
                    ModelInstance a = modelInstances.get(raycastHitMeshIndex);
                    TileHighlighter.setModelInstanceHighlightTemporarily(a);
                    currentlyHighlightedTile = mapTiles.get(raycastHitMeshIndex);
                } else {
                    currentlyHighlightedTile = null;
                }

                modelBatch.render(modelInstances, environment);
                break;
            }

            case NODE: {

                for (Node node : getNodeGraph().vertexSet()) {

                    Vector3 hitpoint = Vector3.Zero;
                    if (Intersector.intersectRaySphere(ray, node.getPosition().cpy().add(0, 0.2f, 0), 0.5f, hitpoint)) {
                        if (node != currentlyHighlightedNode) {
                            currentlyHighlightedNode = node;

                            Model sphereModel = new ModelBuilder().createSphere(
                                    1f, 1f, 1f,
                                    10, 10,
                                    new Material(
                                            ColorAttribute.createDiffuse(new Color(1f, 0f, 0f, 0.35f)),
                                            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                                                    0.55f)),
                                    Usage.Position | Usage.Normal);

                            highlightedNodeModelInstance = new ModelInstance(sphereModel);
                            highlightedNodeModelInstance.transform
                                    .setToTranslation(node.getPosition().cpy().add(0, 0.2f, 0));
                        }
                        modelBatch.render(highlightedNodeModelInstance);
                        return;
                    }

                }
                currentlyHighlightedNode = null;
                break;
            }

            case EDGE: {
                for (Edge edge : getNodeGraph().edgeSet()) {
                    if (edge.getBoundingCylinder().intersects(ray)) {
                        if (edge != currentlyHighlightedEdge) {
                            currentlyHighlightedEdge = edge;

                            highlightedEdgeModelInstance = edge.getBoundingCylinder().toModelInstance(
                                    new ModelBuilder(),
                                    new Material(
                                            ColorAttribute.createDiffuse(new Color(1f, 0f, 0f, 0.35f)),
                                            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                                                    0.55f)));
                        }
                        modelBatch.render(highlightedEdgeModelInstance);
                        return;
                    }
                }
                currentlyHighlightedEdge = null;
                break;
            }

            case HARBOUR: {
                int raycastHitMeshIndex = 0;
                for (TileMesh currentTileMesh : harbourTileMeshes) {

                    Vector3 hitpoint = Vector3.Zero;
                    if (currentTileMesh.rayIntersectsHex(ray, hitpoint)) {
                        raycastHit = true;
                        break;
                    }
                    raycastHitMeshIndex++;
                }

                if (raycastHit) {
                    ModelInstance a = harbourModelInstances.get(raycastHitMeshIndex);
                    TileHighlighter.setModelInstanceHighlightTemporarily(a);
                    currentlyHighlightedHarbourTile = harbourTiles.get(raycastHitMeshIndex);
                } else {
                    currentlyHighlightedHarbourTile = null;
                }

                modelBatch.render(harbourModelInstances, environment);
                break;
            }
        }
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        bandit.render(modelBatch);
        highlightObjectUnderMouse(modelBatch, environment);
        modelBatch.render(modelInstances, environment);
        modelBatch.render(harbourModelInstances, environment);

        // Häfen Bilder rendern
        for (HarbourTile harbour : harbourTiles) {
            ModelBuilder modelBuilder = new ModelBuilder();

            Material material = new Material(
                    TextureAttribute
                            .createDiffuse(catanAssetManager.getTexture(harbour.getMaterialType().getFileName())), // Textur
                                                                                                                   // als
                                                                                                                   // "Haut"
                    new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

            long attributes = VertexAttributes.Usage.Position |
                    VertexAttributes.Usage.Normal |
                    VertexAttributes.Usage.TextureCoordinates;

            Model texturedRectModel = modelBuilder.createRect(
                    -0.9f, 0f, -0.9f, // Bottom Left (x, y, z)
                    0.9f, 0f, -0.9f, // Bottom Right
                    0.9f, 0f, 0.9f, // Top Right
                    -0.9f, 0f, 0.9f, // Top Left
                    0f, 1f, 0f, // Normal (nach oben, +Y)
                    material,
                    attributes);

            ModelInstance rectInstance = new ModelInstance(texturedRectModel);
            rectInstance.transform.setToTranslation(harbour.getWorldPosition().cpy().add(0, 0.25f, 0)); // Position im
                                                                                                        // 3D-Raum
            rectInstance.transform.rotate(Vector3.X, 180f); // Rotation (optional)
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            modelBatch.render(rectInstance);
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

    }

    /**
     * 2D Rendering - hier werden gerade nur die Zahlen der Tiles gerendert
     */
    @Override
    public void render2D(SpriteBatch spriteBatch, Environment environment, Camera camera) {
        for (Tile t : mapTiles) {
            Vector3 textPosition = new Vector3(t.getWorldPosition().x, 1.5f, t.getWorldPosition().z);
            Vector3 screenCoords = new Vector3(textPosition);
            camera.project(screenCoords);

            int tileNumberValue = t.getNumberValue();

            if (tileNumberValue == 7)
                continue;

            if (tileNumberValue == 6 | tileNumberValue == 8) {
                float r = 207 / 255f;
                float g = 52 / 255f;
                float b = 22 / 255f;
                bitmapFont.setColor(new Color(r, g, b, 1));
            }
            if (t.isRobberPlaced()) {
                bitmapFont.setColor(Color.BLACK);
            }

            GlyphLayout layout = new GlyphLayout();
            layout.setText(bitmapFont, String.valueOf(tileNumberValue));

            if (screenCoords.z > 0 && screenCoords.z < 1) {
                bitmapFont.draw(spriteBatch, String.valueOf(tileNumberValue), screenCoords.x - layout.width / 2,
                        screenCoords.y + layout.height / 2);
            }
            bitmapFont.setColor(Color.WHITE);

            for (HarbourTile tile : harbourTiles) {

            }
        }
    }

    @Override
    public void tick(float delta) {
    }

    public Graph<Node, Edge> getNodeGraph() {
        return nodeGraph;
    }

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void dispose() {
    }

    public void setHighlightingType(HighlightingType highlightingType) {
        this.highlightingType = highlightingType;
    }

    public HighlightingType getHighlightingType() {
        return highlightingType;
    }

    /**
     * @return Gibt das Tile zurück, das aktuell unter der Maus ist
     */
    public Optional<Tile> getCurrentlyHighlightedTile() {
        return currentlyHighlightedTile == null ? Optional.empty() : Optional.of(currentlyHighlightedTile);
    }

    public Optional<HarbourTile> getCurrentlyHighlightedHarbour() {
        return currentlyHighlightedHarbourTile == null ? Optional.empty()
                : Optional.of(currentlyHighlightedHarbourTile);
    }

    /**
     * @return Gibt die Node (Ecke) zurück, die aktuell unter der Maus ist
     *         ist gerade keine Ecke unter der Maus, so wird ein leeres Optional
     *         zurückgegeben
     */
    public Optional<Node> getCurrentlyHighlightedNode() {
        return currentlyHighlightedNode == null ? Optional.empty() : Optional.of(currentlyHighlightedNode);
    }

    /**
     * @return Gibt die Edge (Kante) zurück, die aktuell unter der Maus ist
     *         ist gerade keine Kante unter der Maus, so wird ein leeres Optional
     *         zurückgegeben
     */
    public Optional<Edge> getCurrentlyHighlightedEdge() {
        return currentlyHighlightedEdge == null ? Optional.empty() : Optional.of(currentlyHighlightedEdge);
    }

    public boolean isSomethingHighlighted() {
        switch (getHighlightingType()) {
            case TILE:
                return currentlyHighlightedTile != null;
            case EDGE:
                return currentlyHighlightedEdge != null;
            case NODE:
                return currentlyHighlightedNode != null;
            case HARBOUR:
                return currentlyHighlightedHarbourTile != null;
            case NONE:
            default:
                return false;
        }
    }

    public List<Tile> getMapTiles() {
        return mapTiles;
    }

    public boolean canNodeBuildingBePlaced(NodeBuilding nodeBuilding) {

        boolean nodeIsOccupied = false;
        boolean nodeOccupiedByCurrentPlayer = false;
        for (Building building : buildings) {
            if (!(building instanceof NodeBuilding))
                continue;
            NodeBuilding foundNodeBuilding = (NodeBuilding) building;
            if (foundNodeBuilding.getPosition().equals(nodeBuilding.getPosition())) {
                if (foundNodeBuilding.getBuildingType() == BuildingType.HARBOUR)
                    continue; // Harbours should not intervene Settlements
                nodeIsOccupied = true; // There is already a building on this node
                nodeOccupiedByCurrentPlayer = foundNodeBuilding.getPlayer().equals(nodeBuilding.getPlayer());
                break; // No need to check further, we found a building on this node
            }
        }

        switch (nodeBuilding.getBuildingType()) {
            case SETTLEMENT:
                return !nodeIsOccupied;
            case CITY:
                return nodeIsOccupied && nodeOccupiedByCurrentPlayer; // A city can only be placed on a node with a
                                                                      // settlement of the same player
            case HARBOUR:
            default:
                return false;
        }
    }

    public boolean canStreetBePlacedOnEdge(Edge edge) {
        if (edge == null)
            return false;
        for (Building building : buildings) {
            if (!(building instanceof BuildingStreet))
                continue;
            BuildingStreet street = (BuildingStreet) building;
            if (street.getPosition().equals(edge)) {
                return false; // There is already a street on this edge
            }
        }
        return true; // No street with this edge as position found
    }

    public List<NodeBuilding> getNodeBuildingsOnTile(Tile tile) {
        List<NodeBuilding> nodeBuildingsOnTile = new ArrayList<>();
        for (Building building : buildings) {
            if (building.getBuildingType() != BuildingType.SETTLEMENT
                    && building.getBuildingType() != BuildingType.CITY)
                continue;
            NodeBuilding nodeBuilding = (NodeBuilding) building;
            Node position = nodeBuilding.getPosition();

            Set<Tile> neighbourTiles = position.getNeighbourTiles();
            if (neighbourTiles.contains(tile)) {
                nodeBuildingsOnTile.add(nodeBuilding);
            }
        }
        return nodeBuildingsOnTile;
    }
}

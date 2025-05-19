package de.svenojo.catan.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.IRenderable2D;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.math.Triangle;
import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.building.Building;
import de.svenojo.catan.world.building.BuildingCalculator;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.building.NodeBuilding;
import de.svenojo.catan.world.building.buildings.BuildingCity;
import de.svenojo.catan.world.building.buildings.BuildingSettlement;
import de.svenojo.catan.world.building.buildings.BuildingStreet;
import de.svenojo.catan.world.map.MapGenerator;
import de.svenojo.catan.world.tile.Tile;
import de.svenojo.catan.world.tile.TileHighlighter;
import de.svenojo.catan.world.tile.TileMesh;
import de.svenojo.catan.world.tile.TileType;

public class WorldMap implements IRenderable, IRenderable2D, ITickable {
    
    private List<Tile> mapTiles;
    private ArrayList<ModelInstance> modelInstances;
    private ArrayList<TileMesh> tileMeshes;
    private Tile currentlyHighlightedTile = null; 

    private List<Node> nodes;
    private Set<Building> buildings;
    private Graph<Node, Edge> nodeGraph;


    private CatanAssetManager catanAssetManager;

    private BitmapFont bitmapFont;

    private BuildingCalculator buildingCalculator;

    // TODO: MapGeneration auslagern in einen MapGenerator
    public WorldMap(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
        this.bitmapFont = null;
        mapTiles = new ArrayList<>();
        modelInstances = new ArrayList<>();
        tileMeshes = new ArrayList<>();
        nodes = new ArrayList<>();
        buildings = new HashSet<>();
        this.buildingCalculator = new BuildingCalculator(catanAssetManager);

        nodeGraph = GraphTypeBuilder
            .<Node, Edge> undirected().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(Edge.class).weighted(false).buildGraph();
    }   

    /**
     * Generiert die Karte über die MapGenerator Klasse
     */
    public void generateMap() {
        MapGenerator.generateMap(mapTiles, nodeGraph, nodes);
    }

    public void loadAssets() {

        for(Tile worldTile : mapTiles) {
            Model worldTileModel = catanAssetManager.getAssetManager().get(worldTile.getWorldTileType().getFileName(), Model.class);
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

        }

        bitmapFont = catanAssetManager.worldMapIslandNumberFont;
    }

    public void placeBuilding(Player player, Building building) {
        ModelInstance modelInstance = buildingCalculator.calculateBuildingModelInstance(player, building, nodeGraph);
        if(modelInstance != null) {
            buildings.add(building);
            modelInstances.add(modelInstance);
        }
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {

        Ray ray = modelBatch.getCamera().getPickRay(Gdx.input.getX(), Gdx.input.getY());

        boolean raycastHit = false;
        int triangleMeshIndex = 0;
        for(TileMesh currentTileMesh : tileMeshes) {
            
            Vector3 hitpoint = Vector3.Zero;
            if(currentTileMesh.rayIntersectsHex(ray, hitpoint)) {
                raycastHit = true;
                break;
            }
            triangleMeshIndex++;
        }

        if(raycastHit) {
            ModelInstance a = modelInstances.get(triangleMeshIndex);
            TileHighlighter.setModelInstanceHighlightTemporarily(a);
            currentlyHighlightedTile = mapTiles.get(triangleMeshIndex);
            System.out.println(currentlyHighlightedTile.getWorldTileType().toString());
        } else {
            currentlyHighlightedTile = null;
        }
        
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

    public void dispose() {}

    /**
     * @return Gibt das Tile zurück, das aktuell unter der Maus ist
     */
    public Optional<Tile> getCurrentlyHighlightedTile() {
        return currentlyHighlightedTile == null ? Optional.empty() : Optional.of(currentlyHighlightedTile);
    }
}

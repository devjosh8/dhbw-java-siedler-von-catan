package de.svenojo.catan.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import de.svenojo.catan.core.Catan;
import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.math.AxialVector;
import de.svenojo.catan.resources.CatanAssetManager;

public class WorldMap implements IRenderable, ITickable {
    
    private List<WorldTile> mapTiles;
    private List<ModelInstance> modelInstances;

    private CatanAssetManager catanAssetManager;

    public WorldMap(CatanAssetManager catanAssetManager) {
        mapTiles = new ArrayList<>();
        modelInstances = new ArrayList<>();
        this.catanAssetManager = catanAssetManager;
    }

    public void generateMap() {
        int mapRadius = 2;

        for (int q = -mapRadius; q <= mapRadius; q++) {
            for (int r = Math.max(-mapRadius, -q - mapRadius); r <= Math.min(mapRadius, -q + mapRadius); r++) {
                AxialVector tilePosition = new AxialVector(q, r);
                WorldTile worldTile = new WorldTile(tilePosition, WorldTileType.WOODS);
                mapTiles.add(worldTile);
            }
        }

        List<String> alreadyLoading = new ArrayList<>();
        for(WorldTile worldTile : mapTiles) {
            if(!alreadyLoading.contains(worldTile.getWorldTileType().getFileName())) {
                alreadyLoading.add(worldTile.getWorldTileType().getFileName());
                catanAssetManager.getAssetManager().load(worldTile.getWorldTileType().getFileName(), Model.class);
            }
        }
    }

    public void loadModels() {
        for(WorldTile worldTile : mapTiles) {
           Model worldTileModel = catanAssetManager.getAssetManager().get(worldTile.getWorldTileType().getFileName(), Model.class);
           ModelInstance modelInstance = new ModelInstance(worldTileModel);
           modelInstance.transform.setToTranslation(worldTile.getWorldPosition().x, 0, worldTile.getWorldPosition().y);
           modelInstance.transform.scale(WorldTile.WORLD_TILE_SCALE, WorldTile.WORLD_TILE_SCALE, WorldTile.WORLD_TILE_SCALE);
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

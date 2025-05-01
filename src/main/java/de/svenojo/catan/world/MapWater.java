package de.svenojo.catan.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.resources.CatanAssetManager;

public class MapWater implements ITickable, IRenderable {

    private final float WATER_PLANE_SIZE = 100.0f;
    private final float WATER_PLANE_HEIGHT = 0.0f;

    private ModelInstance waterPlaneInstance;

    private CatanAssetManager catanAssetManager;

    public MapWater(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
    }

    public void loadAssets() {
        ModelBuilder modelBuilder = new ModelBuilder();

        float water_plane_size_half = WATER_PLANE_SIZE / 2.0f;

        Model waterPlaneModel = modelBuilder.createRect(
            -water_plane_size_half, WATER_PLANE_HEIGHT, water_plane_size_half,
            water_plane_size_half, WATER_PLANE_HEIGHT, water_plane_size_half,
            water_plane_size_half, WATER_PLANE_HEIGHT,  -water_plane_size_half,
            -water_plane_size_half, WATER_PLANE_HEIGHT,  -water_plane_size_half,
            0f, 1f, 0f,
            new Material(ColorAttribute.createDiffuse(Color.BLUE)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        waterPlaneInstance = new ModelInstance(waterPlaneModel);
    }   

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(waterPlaneInstance);
    }

    @Override
    public void tick(float delta) {

    }
    
}

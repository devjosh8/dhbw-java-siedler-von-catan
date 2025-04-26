package de.svenojo.catan.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.WorldMap;

public class Catan extends Game {

    /**
     * TODO: Kamera und Environment in eigene Klasse auslagern (Renderer.java oder sowat)
     */

    private PerspectiveCamera perspectiveCamera;
    private ModelBatch modelBatch;
    private Environment environment;
    private CameraInputController cameraInputController;

    private WorldMap worldMap;
    private CatanAssetManager catanAssetManager;
    private boolean assetsLoading;

    @Override
    public void create() {
        modelBatch = new ModelBatch();

        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(0f, 20f, 0f);
        perspectiveCamera.lookAt(0, 0f, 0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        cameraInputController = new CameraInputController(perspectiveCamera);
        cameraInputController.rotateLeftKey = 0;
        cameraInputController.rotateRightKey = 0;
        cameraInputController.forwardKey = 0;
        cameraInputController.backwardKey = 0;
        Gdx.input.setInputProcessor(cameraInputController);

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        catanAssetManager = new CatanAssetManager();
        worldMap = new WorldMap(catanAssetManager);
        worldMap.generateMap();
        assetsLoading = true;
    }

    private void doneLoading() {
        worldMap.loadModels();

        assetsLoading = false;
    }
    
    @Override
    public void render() {
        if(assetsLoading && catanAssetManager.getAssetManager().update()) doneLoading();
        
        cameraInputController.update();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(perspectiveCamera);
        if(!assetsLoading) {
            worldMap.render(modelBatch, environment);
        } 
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void resize(int width, int height) {}
}
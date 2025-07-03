package de.svenojo.catan.screen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import de.svenojo.catan.core.CatanGame;
import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.MapWater;
import de.svenojo.catan.world.WorldMap;
import de.svenojo.catan.world.building.buildings.BuildingSettlement;
import de.svenojo.catan.world.building.buildings.BuildingStreet;
import de.svenojo.util.PlayerOptions;

public class GameScreen implements Screen {
    /**
     * TODO: Kamera und Environment in eigene Klasse auslagern (Renderer.java oder sowat)
     */

    private PerspectiveCamera perspectiveCamera;
    private ModelBatch modelBatch;
    private SpriteBatch spriteBatch;
    private Environment environment;
    private CameraInputController cameraInputController;

    private WorldMap worldMap;
    private MapWater mapWater;
    private CatanAssetManager catanAssetManager;
    private boolean assetsLoading;

    private CatanGame catanGame;

    public GameScreen(CatanGame catanGame, PlayerOptions playerOptions) {
        this.catanGame = catanGame;
        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(0f, 20f, 0f);
        perspectiveCamera.lookAt(0, 0f, 0);
        perspectiveCamera.near = 0.1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        cameraInputController = new CameraInputController(perspectiveCamera);
        cameraInputController.rotateLeftKey = 0;
        cameraInputController.rotateRightKey = 0;
        cameraInputController.forwardKey = 0;
        cameraInputController.backwardKey = 0;
        Gdx.input.setInputProcessor(cameraInputController);

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        catanAssetManager = catanGame.getCatanAssetManager();

        worldMap = new WorldMap(catanAssetManager);
        worldMap.generateMap();
        mapWater = new MapWater(catanAssetManager);
        assetsLoading = true;
    }

    private void doneLoading() {
        worldMap.loadAssets();
        mapWater.loadAssets();

        // TODO: zum späteren Zeitpunkt entfernen
        // Testweise Buildings hinzufügen
        Player player = new Player("bob", Color.RED);
        for(Edge node : worldMap.getNodeGraph().edgeSet()) {
            if(new Random().nextInt(3) == 0) {
                //BuildingStreet street = new BuildingStreet(player, edge);
                BuildingSettlement settlement = new BuildingSettlement(player, worldMap.getNodeGraph().getEdgeSource(node));
                worldMap.placeBuilding(player, settlement);
            }
        }

        assetsLoading = false;
    }
    
    @Override
    public void render(float delta) {
        if(assetsLoading && catanAssetManager.getAssetManager().update()) doneLoading();
        
        cameraInputController.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(perspectiveCamera);
        if(!assetsLoading) {
            worldMap.render(modelBatch, environment);
            mapWater.render(modelBatch, environment);
        } 
        modelBatch.end();

        spriteBatch.begin();
        if(!assetsLoading) {
            worldMap.render2D(spriteBatch, environment, perspectiveCamera);
        } 
        spriteBatch.end();
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
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {

    }


    @Override
    public void hide() {

    }
}

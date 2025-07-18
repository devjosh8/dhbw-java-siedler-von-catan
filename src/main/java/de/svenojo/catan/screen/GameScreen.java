package de.svenojo.catan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.google.common.eventbus.EventBus;

import de.svenojo.catan.core.CatanGame;
import de.svenojo.catan.logic.CatanGameLogic;
import de.svenojo.catan.logic.PlacementInputProcessor;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.screen.ui.GameUI;
import de.svenojo.catan.world.MapWater;
import de.svenojo.catan.world.WorldMap;
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
    private CatanGameLogic catanGameLogic;

    private EventBus gameScreenEventBus = new EventBus();

    private GameUI gameUI;

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
        cameraInputController.rotateButton = Input.Buttons.RIGHT; // use right for rotate
        cameraInputController.translateButton = Input.Buttons.MIDDLE; // use middle for translate
        cameraInputController.forwardButton = -1;

        
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        gameUI = new GameUI(gameScreenEventBus);

        catanAssetManager = catanGame.getCatanAssetManager();

        worldMap = new WorldMap(catanAssetManager);
        worldMap.generateMap();
        mapWater = new MapWater(catanAssetManager);
        assetsLoading = true;

        catanGameLogic = new CatanGameLogic(catanGame, playerOptions.getplayerList(), worldMap, gameUI, gameScreenEventBus);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        PlacementInputProcessor placementInputProcessor = new PlacementInputProcessor(catanGameLogic, worldMap);
        inputMultiplexer.addProcessor(gameUI.getStage());
        inputMultiplexer.addProcessor(placementInputProcessor);
        inputMultiplexer.addProcessor(cameraInputController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void doneLoading() {
        worldMap.loadAssets();
        mapWater.loadAssets();

        //Start game logic
        assetsLoading = false;
    }
    
    private CatanGameLogic.GameState lastGameState = null;
    private CatanGameLogic.RoundPhase lastRoundPhase = null;
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

        if (catanGameLogic.getCurrentGameState() != lastGameState) {
            lastGameState = catanGameLogic.getCurrentGameState();
            catanGameLogic.playGameState();
        }
        if (catanGameLogic.getCurrentRoundPhase() != lastRoundPhase) {
            lastRoundPhase = catanGameLogic.getCurrentRoundPhase();
            catanGameLogic.playRoundPhase();
        }

        gameUI.render(delta);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        worldMap.dispose();

        gameUI.dispose();
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

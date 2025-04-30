package de.svenojo.catan.core;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.screen.MainMenuScreen;

public class CatanGame extends Game {

    /**
     * TODO: Kamera und Environment in eigene Klasse auslagern (Renderer.java oder sowat)
     */

    private CatanAssetManager catanAssetManager;

    @Override
    public void create() {
        catanAssetManager = new CatanAssetManager();
        catanAssetManager.initializeAssets();

        /**
         *  Blockiert den Thread bis alle Assets geladen sind
         * TODO: eventuell Ladescreen siehe https://libgdx.com/wiki/managing-your-assets
         */
        catanAssetManager.getAssetManager().finishLoading();


        setScreen(new MainMenuScreen(this));
    }
    
    @Override
    public void render() {
        getScreen().render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        getScreen().dispose();
    }

    @Override
    public void pause() {
        getScreen().pause();
    }

    @Override
    public void resume() {
        getScreen().resume();
    }

    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }

    public CatanAssetManager getCatanAssetManager() {
        return catanAssetManager;
    }
}
package de.svenojo.catan.core;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import de.svenojo.catan.screen.MainMenuScreen;

public class CatanGame extends Game {

    /**
     * TODO: Kamera und Environment in eigene Klasse auslagern (Renderer.java oder sowat)
     */

    @Override
    public void create() {
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
    public void resume() {}

    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }
}
package de.svenojo.catan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.svenojo.catan.core.CatanGame;
import de.svenojo.catan.resources.CatanAssetManager;

public class CreditScreen implements Screen {

    private final CatanGame catanGame;
    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;

    private CatanAssetManager catanAssetManager;

    public CreditScreen(CatanGame catanGame) {
        this.catanGame = catanGame;

        catanAssetManager = catanGame.getCatanAssetManager();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        backgroundTexture = new Texture(Gdx.files.internal("data/images/blurred_dark_background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label creditsTitle = new Label("Entwickelt von:", skin, "title");
        creditsTitle.getStyle().fontColor = Color.WHITE;
        creditsTitle.setAlignment(Align.center);
        Label creditsNames = new Label("Sven Schraeer\nJoshua Kandel\n& Nora Streile\n© 2025", skin, "default");
        creditsNames.getStyle().fontColor = Color.WHITE;
        creditsNames.setFontScale(2f);
        creditsNames.setAlignment(Align.center);

        TextButton backButton = new TextButton("Zuruck zum Hauptmenu", skin);
        backButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                this.catanGame.setScreen(new MainMenuScreen(this.catanGame));
                return true;
            }
            return false;
        });
        backButton.getLabel().setFontScale(2f);

        table.add(creditsTitle).padBottom(20);
        table.row();
        table.add(creditsNames).padBottom(80);
        table.row();
        table.add(backButton).width(540).height(80);

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}

package de.svenojo.catan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private BitmapFont bitmapFont;

    public CreditScreen(CatanGame catanGame) {
        this.catanGame = catanGame;

        catanAssetManager = catanGame.getCatanAssetManager();

        bitmapFont = catanAssetManager.mainFontWithoutBorder;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        backgroundTexture = new Texture(Gdx.files.internal("data/images/blurred_dark_background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label.LabelStyle namesStyle = new Label.LabelStyle(bitmapFont, Color.WHITE);

        TextButton.TextButtonStyle baseButtonStyle = skin.get(TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = baseButtonStyle.up;
        customButtonStyle.down = baseButtonStyle.down;
        customButtonStyle.over = baseButtonStyle.over;
        customButtonStyle.checked = baseButtonStyle.checked;
        customButtonStyle.disabled = baseButtonStyle.disabled;
        customButtonStyle.font = bitmapFont;


        Label creditsTitle = new Label("Entwickelt von:", skin, "title");
        creditsTitle.getStyle().fontColor = Color.WHITE;
        creditsTitle.setFontScale(2f);
        creditsTitle.setAlignment(Align.center);
        Label creditsNames = new Label("Sven Schräer\nJoshua Kandel\n& Nora Streile\n© 2025", namesStyle);
        creditsNames.setAlignment(Align.center);

        TextButton backButton = new TextButton("Zurück zum Hauptmenü", customButtonStyle);
        backButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                this.catanGame.setScreen(new MainMenuScreen(this.catanGame));
                return true;
            }
            return false;
        });
        backButton.getLabel().setFontScale(1f);

        table.add(creditsTitle).padBottom(20);
        table.row();
        table.add(creditsNames).padBottom(80);
        table.row();
        table.add(backButton).width(560).height(80);

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

package de.svenojo.catan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;


public class EndScreen implements Screen {
    private final CatanGame catanGame;
    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;

    private CatanAssetManager catanAssetManager;
    private BitmapFont bitmapFont;

    private Sound clickSound;
    private Player winningPlayer;


    public EndScreen(CatanGame catanGame, Player player) {
        this.catanGame = catanGame;

        this.winningPlayer = player;
        catanAssetManager = catanGame.getCatanAssetManager();

        bitmapFont = catanAssetManager.mainFontWithoutBorder;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        backgroundTexture = new Texture(Gdx.files.internal("data/images/blurred_dark_background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        clickSound = catanAssetManager.getSound("data/sounds/click_sound.wav");

        Label.LabelStyle namesStyle = new Label.LabelStyle(bitmapFont, Color.WHITE);

        Label congratsTitle = new Label("Geschafft! Das Spiel ist beendet!", skin, "title");
        congratsTitle.getStyle().fontColor = Color.WHITE;
        congratsTitle.setFontScale(2f);
        congratsTitle.setAlignment(Align.center);
        congratsTitle.setWrap(true);

        String winnerName = winningPlayer.getName();
        Label congratulations = new Label("Herzlichen Glückwunsch, " + winnerName + ", als Erster hast du 10 Punkte erreicht und das Spiel damit gewonnen!", namesStyle);
        congratulations.setAlignment(Align.center);
        congratulations.setWrap(true);


        TextButton.TextButtonStyle baseButtonStyle = skin.get(TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = baseButtonStyle.up;
        customButtonStyle.down = baseButtonStyle.down;
        customButtonStyle.over = baseButtonStyle.over;
        customButtonStyle.checked = baseButtonStyle.checked;
        customButtonStyle.disabled = baseButtonStyle.disabled;
        customButtonStyle.font = bitmapFont;

        TextButton backButton = new TextButton("Zurück zum Hauptmenü", customButtonStyle);
        backButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                this.catanGame.setScreen(new MainMenuScreen(this.catanGame));
                return true;
            }
            return false;
        });
        backButton.getLabel().setFontScale(1f);

        TextButton exitButton = new TextButton("Spiel verlassen", customButtonStyle);
        exitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                Gdx.app.exit();
                return true;
            }
            return false;
        });
        backButton.getLabel().setFontScale(1f);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(congratsTitle).width(800).padBottom(20);
        root.row();
        root.add(congratulations).width(500).padBottom(80);
        root.row();
        root.add(backButton).width(560/2).height(80/2).padBottom(20);
        root.row();
        root.add(exitButton).width(560/2).height(80/2);
        stage.addActor(backgroundImage);
        stage.addActor(root);
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
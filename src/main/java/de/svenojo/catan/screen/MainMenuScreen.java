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
import de.svenojo.catan.resources.CatanAssetManager;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;

    private CatanGame catanGame;
    private Texture backgroundTexture;

    private CatanAssetManager catanAssetManager;
    private BitmapFont bitmapFontWithBorder;
    private BitmapFont bitmapFontWithoutBorder;

    private Sound clickSound;

    public MainMenuScreen(CatanGame catanGame) {
        this.catanGame = catanGame;

        catanAssetManager = catanGame.getCatanAssetManager();

        bitmapFontWithBorder = catanAssetManager.mainFontWithBorder;
        bitmapFontWithoutBorder = catanAssetManager.mainFontWithoutBorder;
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));
        
        backgroundTexture = new Texture(Gdx.files.internal("data/images/Siedler_von_Catan.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        Label.LabelStyle introductionStyle = new Label.LabelStyle(bitmapFontWithBorder, Color.WHITE);
        
        Label introduction = new Label("Ein Land voller Möglichkeiten liegt vor euch - wild, unberührt und bereit, erobert zu werden. Erschließt mit Mut und Weitsicht das unentdeckte Land und nutzt eure Ressourcen weise, um Straßen zu bauen, Städte zu gründen und eure Macht auszudehnen. Nur wer klug entscheidet, geschickt verhandelt und mutig expandiert, wird auf Catan zur Legende.", introductionStyle);
        introduction.getStyle().fontColor = Color.WHITE;
        introduction.setAlignment(Align.center);
        introduction.setWrap(true);
        
        
        TextButton.TextButtonStyle baseButtonStyle = skin.get(TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = baseButtonStyle.up;
        customButtonStyle.down = baseButtonStyle.down;
        customButtonStyle.over = baseButtonStyle.over;
        customButtonStyle.checked = baseButtonStyle.checked;
        customButtonStyle.disabled = baseButtonStyle.disabled;
        customButtonStyle.font = bitmapFontWithoutBorder;
        
        TextButton startButton = new TextButton("Neues Spiel", customButtonStyle);
        startButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                this.catanGame.setScreen(new PlayerSelectorScreen(this.catanGame));
                return true;
            }
            return false;
        });


        TextButton creditButton = new TextButton("Credits", customButtonStyle);
        creditButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                this.catanGame.setScreen(new CreditScreen(this.catanGame));
                return true;
            }
            return false;
        });


        TextButton exitButton = new TextButton("Spiel verlassen", customButtonStyle);
        exitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                Gdx.app.exit();
                return true;
            }
            return false;
        });

        clickSound = catanAssetManager.getSound("data/sounds/click_sound.wav");


        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(introduction).width(1500/2).padTop(150).padBottom(50);;
        root.row();
        root.add(startButton).width(450/2).height(80/2);
        root.row();
        root.add(creditButton).width(450/2).height(80/2).padTop(20);
        root.row();
        root.add(exitButton).width(450/2).height(80/2).padTop(20);

        stage.addActor(backgroundImage);
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
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
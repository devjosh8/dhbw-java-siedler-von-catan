package de.svenojo.catan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.svenojo.catan.core.CatanGame;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;

    private CatanGame catanGame;

    public MainMenuScreen(CatanGame catanGame) {
        this.catanGame = catanGame;

        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));
        
        Texture backgroundTexture = new Texture(Gdx.files.internal("data/images/background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        TextButton.TextButtonStyle originalStyle = skin.get(TextButton.TextButtonStyle.class);

        // Kopiere ihn (optional, damit andere Buttons nicht betroffen sind)
        TextButton.TextButtonStyle largerStyle = new TextButton.TextButtonStyle();
        largerStyle.up = originalStyle.up;
        largerStyle.down = originalStyle.down;
        largerStyle.over = originalStyle.over;
        largerStyle.checked = originalStyle.checked;
        largerStyle.font = originalStyle.font;

        // Vergrößere die Schrift
        largerStyle.font.getData().setScale(2f); // z. B. doppelte Größe

        // Erstelle Button mit angepasstem Stil
        TextButton startButton = new TextButton("Spiel starten", largerStyle);

        startButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                this.catanGame.setScreen(new GameScreen(this.catanGame));
                return true;
            }
            return false;
        });


        TextButton creditButton = new TextButton("Credits", largerStyle);

        creditButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                this.catanGame.setScreen(new CreditScreen(this.catanGame));
                return true;
            }
            return false;
        });

        TextButton exitButton = new TextButton("Spiel verlassen", largerStyle);

        exitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                Gdx.app.exit();
                return true;
            }
            return false;
        });



        // Zentriere den Button mit Table
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(startButton).width(650).height(80);
        table.row();
        table.add(creditButton).width(650).height(80).padTop(20);
        table.row();
        table.add(exitButton).width(650).height(80).padTop(20);

        stage.addActor(backgroundImage);
        stage.addActor(table);
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
    }
    
}

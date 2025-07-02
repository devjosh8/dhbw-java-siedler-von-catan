package de.svenojo.catan.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.svenojo.catan.core.CatanGame;
import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;

public class PlayerSelectorScreen implements Screen {

    private CatanGame catanGame;
    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;

    private CatanAssetManager catanAssetManager;
    private BitmapFont bitmapFontWithBorder;
    private BitmapFont bitmapFont;

    private Sound clickSound;

    private final List<Player> players = new ArrayList<>();
    private final List<Color> availableColors = List.of(Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN);


    public PlayerSelectorScreen(CatanGame catanGame) {
        this.catanGame = catanGame;
    }

    
    @Override
    public void show() {
        catanAssetManager = catanGame.getCatanAssetManager();

        bitmapFontWithBorder = catanAssetManager.mainFontWithBorder;
        bitmapFont = catanAssetManager.mainFontWithoutBorder;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));
        
        backgroundTexture = new Texture(Gdx.files.internal("data/images/blurred_dark_background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        clickSound = catanAssetManager.getSound("data/sounds/click_sound.wav");


        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);
        root.defaults().pad(10);

        stage.addActor(backgroundImage);
        stage.addActor(root);

        Label.LabelStyle descriptionStyle = new Label.LabelStyle(bitmapFontWithBorder, Color.WHITE);
        Label selectorDescription = new Label("Willkommen in Catan, Abenteurer! Bitte tragt hier eure Namen ein und wählt eine Farbe:", descriptionStyle);
        selectorDescription.getStyle().fontColor = Color.WHITE;
        selectorDescription.setFontScale(1.5f);
        selectorDescription.setAlignment(Align.center);
        root.add(selectorDescription).padBottom(80).row();
        
        Label playerCounterDescription = new Label("Anzahl Spieler:", descriptionStyle);
        selectorDescription.setFontScale(1f);
        root.add(playerCounterDescription);

        SelectBox<Integer> playerCountBox = new SelectBox<>(skin);
        playerCountBox.setItems(2, 3, 4);
        root.add(playerCountBox).row();

        Table playerContainer = new Table();
        playerContainer.defaults().pad(10).top().left();
        root.add(playerContainer).colspan(2).row();

        playerCountBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int count = playerCountBox.getSelected();
                buildPlayerInputs(playerContainer, count);
            }
        });

        buildPlayerInputs(playerContainer, 4);

    
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

        TextButton startButton = new TextButton("Spiel starten", customButtonStyle);
        startButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                this.catanGame.setScreen(new GameScreen(this.catanGame));
                return true;
            }
            return false;
        });


        root.row();
        root.add(startButton).width(560).height(80).padTop(200);
        root.row();
        root.add(backButton).width(560).height(80);

    }


private void buildPlayerInputs(Table container, int count) {
        container.clear();
        players.clear();

        for (int i = 0; i < count; i++) {
            Table playerTable = new Table(skin);
            //playerTable.setBackground("default"); -> wie Hintergrund definieren? default nur Platzhalter

            TextField nameField = new TextField("", skin);
            nameField.setMessageText("Spielername");

            Label colorLabel = new Label("Farbe: ", skin);
            ButtonGroup<TextButton> colorButtons = new ButtonGroup<>();
            Table colorTable = new Table();
            colorTable.defaults().padRight(5);

            for (Color color : availableColors) {
                TextButton colorButton = new TextButton("", skin);
                colorButton.setColor(color);
                colorButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // soll später selected Color aktualisieren
                    }
                });
                colorButtons.add(colorButton);
                colorTable.add(colorButton);
            }

            TextButton confirmButton = new TextButton("Spieler festlegen", skin);
            confirmButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String name = nameField.getText();
                    TextButton selected = colorButtons.getChecked();
                    if (name.isEmpty() || selected == null) {
                        Dialog error = new Dialog("Fehler", skin);
                        error.text("Bitte Namen und Farbe angeben!");
                        error.button("OK");
                        error.show(stage);
                        return;
                    }

                    Color selectedColor = selected.getColor();
                    Player player = new Player(name, selectedColor);
                    players.add(player);
                    /*Konsolenausgaben um richtiges Anlegen der Player zu prüfen:
                    String newplayername = player.getName();
                    Color newplayerColor = player.getColor();
                    System.out.println(newplayername);
                    System.out.println(newplayerColor);
                    */

                    nameField.setDisabled(true);
                    selected.setDisabled(true);
                    confirmButton.setDisabled(true);
                    for (TextButton button : colorButtons.getButtons()) {
                        button.setDisabled(true);
                    }
                }
            });

            playerTable.add("Spieler " + (i + 1)).left().row();
            playerTable.add(nameField).width(200).row();
            playerTable.add(colorLabel).left().row();
            playerTable.add(colorTable).row();
            playerTable.add(confirmButton).padTop(10).row();

            container.add(playerTable).left();
        }
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

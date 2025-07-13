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
import de.svenojo.util.PlayerOptions;

public class PlayerSelectorScreen implements Screen {

    private CatanGame catanGame;
    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;

    private CatanAssetManager catanAssetManager;
    private BitmapFont bitmapFontWithBorder;
    private BitmapFont bitmapFont;

    private Sound clickSound;

    private TextButton startButton;
    private final List<Player> players = new ArrayList<>();
    private int selectedPlayerCount = 4;
    private final List<Color> availableColors = List.of(Color.RED, Color.ROYAL, Color.SKY, Color.ORANGE);
    private final List<ColorButtonEntry> allColorButtons = new ArrayList<>();

    private static class ColorButtonEntry {
        public final TextButton button;
        public final Color color;
    
        public ColorButtonEntry(TextButton button, Color color) {
            this.button = button;
            this.color = color;
        }
    }

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
        root.pad(20/2);
        root.defaults().pad(10/2);

        stage.addActor(backgroundImage);
        stage.addActor(root);

        Label.LabelStyle descriptionStyle = new Label.LabelStyle(bitmapFontWithBorder, Color.WHITE);
        Label welcome = new Label("Willkommen in Catan, Abenteurer!", skin, "title");
        welcome.getStyle().fontColor = Color.WHITE;
        welcome.setFontScale(1f);
        Label selectorDescription = new Label("Bitte tragt hier eure Namen ein und wählt eine Farbe:", descriptionStyle);
        selectorDescription.getStyle().fontColor = Color.WHITE;
        selectorDescription.setFontScale(1f);
        selectorDescription.setAlignment(Align.center);
        root.add(welcome).padBottom(40/2).row();
        root.add(selectorDescription).padBottom(50/2);
        root.row();
        
        Label playerCounterDescription = new Label("Anzahl Spieler:", descriptionStyle);
        selectorDescription.setFontScale(1f);

        SelectBox<Integer> playerCountBox = new SelectBox<>(skin);

        playerCountBox.setItems(4, 3, 2);
        playerCountBox.setSelected(4);

        Table playerCountRow = new Table();
        playerCountRow.add(playerCounterDescription).padRight(30);
        playerCountRow.add(playerCountBox);
        root.add(playerCountRow).center().row();

        Table playerContainer = new Table();
        playerContainer.defaults().pad(40/2).top().left();
        root.add(playerContainer).colspan(2).row();

        playerCountBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedPlayerCount = playerCountBox.getSelected(); 
                buildPlayerInputs(playerContainer, selectedPlayerCount);
                startButton.setDisabled(true);
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
        backButton.getLabel().setFontScale(0.5f);
        backButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                clickSound.play();
                this.catanGame.setScreen(new MainMenuScreen(this.catanGame));
                return true;
            }
            return false;
        });
        backButton.getLabel().setFontScale(1f);

        startButton = new TextButton("Spiel starten", customButtonStyle);
        startButton.getLabel().setFontScale(1f);
        startButton.setDisabled(true);
        startButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                if (players.size() < selectedPlayerCount) {
                    Dialog error = new Dialog("Fehler", skin);
                    error.text("  Bitte zuerst alle Spieler anlegen!  ");
                    error.button("OK");
                    error.setColor(Color.SKY);
                    error.show(stage);
                    return false;
                }
                else {
                    clickSound.play();
                    PlayerOptions PlayerOptions = new PlayerOptions(players);
                    this.catanGame.setScreen(new GameScreen(this.catanGame, PlayerOptions));
                    return true;
                }
            }
            return false;
        });




        root.row();
        root.add(startButton).width(560/2).height(80/2).padTop(200/2);
        root.row();
        root.add(backButton).width(560/2).height(80/2);

    }


private void buildPlayerInputs(Table container, int selectedPlayerCount) {
        container.clear();
        players.clear();
        allColorButtons.clear();

        for (int i = 0; i < selectedPlayerCount; i++) {
            Table playerTable = new Table(skin);

            Label playerLabel = new Label("Spieler " + (i + 1), skin);
            playerLabel.getStyle().fontColor = Color.WHITE;
            playerLabel.setFontScale(0.75f);

            TextField nameField = new TextField("",skin);
            nameField.setMessageText("Spielername");
            nameField.setSize(250/2, 40/2);

            Label colorLabel = new Label("Farbe: ", skin);
            colorLabel.getStyle().fontColor = Color.WHITE;
            colorLabel.setFontScale(0.75f);
            ButtonGroup<TextButton> colorButtons = new ButtonGroup<>();
            Table colorTable = new Table();
            colorTable.defaults().padRight(3);

            List<TextButton> thisPlayersColorButtons = new ArrayList<>();

            for (Color color : availableColors) {
                TextButton colorButton = new TextButton("", skin);
                colorButton.setColor(color);
                colorButton.getLabel().setFontScale(0.5f);
                thisPlayersColorButtons.add(colorButton);

                colorButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // soll später selected Color aktualisieren
                    }
                });
                colorButtons.add(colorButton);
                colorTable.add(colorButton);
                allColorButtons.add(new ColorButtonEntry(colorButton, color));
            }

            TextButton confirmButton = new TextButton("Spieler festlegen", skin);
            confirmButton.getLabel().setColor(Color.WHITE);
            confirmButton.getLabel().setFontScale(0.5f);
            confirmButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String name = nameField.getText();
                    TextButton selected = colorButtons.getChecked();
                    if (name.isEmpty() || selected == null) {
                        Dialog error = new Dialog("Fehler", skin);
                        error.text("  Bitte Namen und Farbe angeben!  ");
                        error.button("OK");
                        error.setColor(Color.SKY);
                        error.show(stage);
                        return;
                    }

                    Color selectedColor = selected.getColor();
                    Player player = new Player(name, selectedColor);
                    players.add(player);

                    for (ColorButtonEntry entry : allColorButtons) {
                        if (entry.color.equals(selectedColor) && !entry.button.isChecked()) {
                            entry.button.setDisabled(true);
                            entry.button.setColor(Color.GRAY);
                        }
                    }
                    /*Konsolenausgaben um richtiges Anlegen der Player zu prüfen:
                    String newplayername = player.getName();
                    Color newplayerColor = player.getColor();
                    System.out.println(newplayername);
                    System.out.println(newplayerColor);
                    */

                    nameField.setDisabled(true);
                    selected.setDisabled(true);
                    confirmButton.setDisabled(true);
                    confirmButton.setColor(Color.GRAY);
                    for (TextButton button : colorButtons.getButtons()) {
                        button.setDisabled(true);
                    }

                    if (players.size() == selectedPlayerCount) {
                        startButton.setDisabled(false);
                    } else {
                        startButton.setDisabled(true);
                    }
                }
            });

            playerTable.add(playerLabel).left().row();
            playerTable.add(nameField).width(300/2).padTop(10/2).row();
            playerTable.add(colorLabel).padTop(40/2).left().row();
            playerTable.add(colorTable).padTop(20/2).row();
            playerTable.add(confirmButton).padTop(50/2).row();

            container.add(playerTable).padTop(50/2).left();
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

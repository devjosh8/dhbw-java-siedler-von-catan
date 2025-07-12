package de.svenojo.catan.screen.ui;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.eventbus.EventBus;

import de.svenojo.catan.logic.events.BuildCityEvent;
import de.svenojo.catan.logic.events.BuildSettlementEvent;
import de.svenojo.catan.logic.events.BuildStreetEvent;
import de.svenojo.catan.logic.events.EndTurnEvent;
import de.svenojo.catan.world.material.MaterialType;
import lombok.Getter;

public class GameUI {
    private final EventBus gameScreenEventBus;

    private Stage stage; 
    private Skin skin;

    private Label currentPlayerLabel;
    private Label rolledNumberLabel;

    @Getter
    private TextButton endTurnButton;
    @Getter
    private Table buttonTable;

    private TextButton buildSettlementButton;
    private TextButton buildCityButton;
    private TextButton buildStreetButton;

    public GameUI(EventBus gameScreenEventBus) {
        this.gameScreenEventBus = gameScreenEventBus;

        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true);

        table.top().left().pad(10);
        
        // Rolled Number Label
        rolledNumberLabel = new Label("Geworfene Zahl: ", skin);
        rolledNumberLabel.setFontScale(2.0f);

        
        table.add(rolledNumberLabel).expandX().left().row();

        // Current Player Label
        currentPlayerLabel = new Label("Spieler: ", skin);
        currentPlayerLabel.setFontScale(1.5f);

        table.add(currentPlayerLabel)
            .width(600)
            .left().row();

        // MAterial Labels
        List<MaterialType> materialTypes = List.of(MaterialType.actualMaterialValues());
        initializeMaterials(materialTypes, table);
        
        // Building Buttons

        buttonTable = new Table();
        buttonTable.bottom().left().pad(10);

        buildSettlementButton = new TextButton("Baue Siedlung", skin);
        buildSettlementButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("DEBUG", "Build Settlement Button clicked");
                    gameScreenEventBus.post(new BuildSettlementEvent());
                }
            }
        );

        buildCityButton = new TextButton("Baue Stadt", skin);
        buildCityButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("DEBUG", "Build City Button clicked");
                    gameScreenEventBus.post(new BuildCityEvent());
                }
            }
        );

        buildStreetButton = new TextButton("Baue Strasse", skin);
        buildStreetButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("DEBUG", "Build Street Button clicked");
                    gameScreenEventBus.post(new BuildStreetEvent());
                }
            }
        );

        buttonTable.add(buildStreetButton).expandX().fillX().padBottom(10).row();
        buttonTable.add(buildSettlementButton).expandX().fillX().padBottom(10).row();
        buttonTable.add(buildCityButton).expandX().fillX().padBottom(10).row();

        table.add(buttonTable).expand().bottom().left().pad(10);


        // End Turn Button
        endTurnButton = new TextButton("Zug beenden", skin);
        endTurnButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("DEBUG", "End Turn Button clicked");
                    gameScreenEventBus.post(new EndTurnEvent());
                }
            }
        );

        endTurnButton.setVisible(false); // Initially hidden, will be shown in the end
        buttonTable.setVisible(false); // Initially hidden

        table.add(endTurnButton).expand().bottom().right().pad(10);
    }

    public Stage getStage() {
        return stage;
    }

    public void updateCurrentPlayer(String currentPlayerDisplay, String color) {
        currentPlayerLabel.setText("Spieler: " + currentPlayerDisplay + " (" + color + ")");
    }

    public void updateRolledNumber(int rolledNumber) {
        rolledNumberLabel.setText("Geworfene Zahl: " + rolledNumber);
    }
    private Map<MaterialType, Label> materialLabels = new HashMap<>();

    public void initializeMaterials(List<MaterialType> materials, Table table) {
        for (MaterialType material : materials) {
            Label materialLabel = new Label(material.name() + ": 0", skin);
            materialLabels.put(material, materialLabel);
            table.add(materialLabel).width(500).left().row();;
        }
    }

    public void updateMaterials(Map<MaterialType, Integer> playerMaterials) {
        for (Map.Entry<MaterialType, Integer> entry : playerMaterials.entrySet()) {
            MaterialType material = entry.getKey();
            int count = entry.getValue();
            Label materialLabel = materialLabels.get(material);
            if (materialLabel != null) {
                Gdx.app.log("DEBUG", "Updating material: " + material.name() + " to count: " + count);
                materialLabel.setText(material.name() + ": " + count);
            }
        }
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
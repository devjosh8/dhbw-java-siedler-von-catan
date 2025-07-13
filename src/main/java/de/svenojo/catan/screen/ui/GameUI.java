package de.svenojo.catan.screen.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
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
import de.svenojo.catan.logic.events.TradeHarbourEvent;
import de.svenojo.catan.logic.events.TryToConfirmTradeEvent;
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
    @Getter
    private Dialog tradeDialog;

    private TextButton buildSettlementButton;
    private TextButton buildCityButton;
    private TextButton buildStreetButton;

    private TextButton harbourTradeButton;

    private Label notificationLabel;

    public GameUI(EventBus gameScreenEventBus) {
        this.gameScreenEventBus = gameScreenEventBus;

        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(false);

        table.top().left().pad(10);

        // Noitification Label
        notificationLabel = new Label("", skin);
        notificationLabel.setFontScale(1.2f);
        notificationLabel.setColor(Color.WHITE);
        notificationLabel.setWrap(true); // Aktiviert Zeilenumbruch

        // Rolled Number Label
        rolledNumberLabel = new Label("Geworfene Zahl: ", skin);
        rolledNumberLabel.setFontScale(2.0f);

        table.add(rolledNumberLabel).expandX().left(); // Rolled number on the left
        table.add(notificationLabel).width(600).expandX().right().row(); // Notification on the right

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
                });

        buildCityButton = new TextButton("Baue Stadt", skin);
        buildCityButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.log("DEBUG", "Build City Button clicked");
                        gameScreenEventBus.post(new BuildCityEvent());
                    }
                });

        buildStreetButton = new TextButton("Baue Strasse", skin);
        buildStreetButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.log("DEBUG", "Build Street Button clicked");
                        gameScreenEventBus.post(new BuildStreetEvent());
                    }
                });

        // Harbour Trader Button
        harbourTradeButton = new TextButton("Handel mit Hafen", skin);
        harbourTradeButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.log("DEBUG", "Harbour Trade Button clicked");
                        gameScreenEventBus.post(new TradeHarbourEvent());
                    }
                });

        // Bank Trade Button
        TextButton bankTradeButton = new TextButton("Handel mit Bank", skin);
        bankTradeButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.log("DEBUG", "Bank Trade Button clicked");

                        buttonTable.setVisible(false); // Hide the button table to show trade dialog
                        showTradeDialog(4, MaterialType.actualMaterialValues(), MaterialType.actualMaterialValues());
                    }
                });

        // Add Harbour Trade Button to the button table
        buttonTable.add(bankTradeButton).expandX().fillX().padBottom(10).row();
        buttonTable.add(harbourTradeButton).expandX().fillX().padBottom(30).row();
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
                });

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
            table.add(materialLabel).width(500).left().row();
            ;
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

    public void showTradeDialog(int amountToGive, MaterialType[] materialsToGive, MaterialType[] materialsToReceive) {
        // Create the dialog
        Gdx.app.log("DEBUG", "Showing trade dialog");
        tradeDialog = new Dialog("Handel", skin);

        if (materialsToGive[0] == MaterialType.NONE) {
            materialsToGive = MaterialType.actualMaterialValues();
        }

        // Create dropdowns for selecting materials
        SelectBox<MaterialType> giveMaterialDropdown = new SelectBox<>(skin);
        giveMaterialDropdown.setItems(materialsToGive); // Populate with all material types

        SelectBox<MaterialType> receiveMaterialDropdown = new SelectBox<>(skin);
        receiveMaterialDropdown.setItems(materialsToReceive); // Populate with all material types

        // Add dropdowns to the dialog
        tradeDialog.getContentTable().add("Geben: (" + amountToGive + ")").pad(10);
        tradeDialog.getContentTable().add(giveMaterialDropdown).pad(10).row();
        tradeDialog.getContentTable().add("Erhalten: (1)").pad(10);
        tradeDialog.getContentTable().add(receiveMaterialDropdown).pad(10).row();

        // Create buttons for confirm and cancel
        TextButton confirmButton = new TextButton("Handel", skin);
        TextButton cancelButton = new TextButton("Abbrechen", skin);

        // Add button listeners
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MaterialType giveMaterial = giveMaterialDropdown.getSelected();
                MaterialType receiveMaterial = receiveMaterialDropdown.getSelected();
                Gdx.app.log("DEBUG", "Trade confirmed: Give " + giveMaterial + ", Receive " + receiveMaterial);

                // Post a trade event or handle the trade logic here
                gameScreenEventBus.post(new TryToConfirmTradeEvent(amountToGive, giveMaterial, receiveMaterial));

                tradeDialog.hide(); // Close the dialog
                buttonTable.setVisible(true); // Show the button table after trade confirmation
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("DEBUG", "Trade canceled");
                tradeDialog.hide(); // Close the dialog
                buttonTable.setVisible(true); // Show the button table after trade cancellation
            }
        });

        // Add buttons to the dialog
        tradeDialog.getButtonTable().add(cancelButton).pad(10);
        tradeDialog.getButtonTable().add(confirmButton).pad(10);

        // Show the dialog
        tradeDialog.show(stage);
    }

    public void showNotification(String message, float duration) {

        notificationLabel.setText(message);
        notificationLabel.setColor(Color.WHITE); // Set the color to white
        notificationLabel.setVisible(true);

        // Schedule hiding the notification after the specified duration
        notificationLabel.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(0.5f),
                Actions.delay(duration / 1000f),
                Actions.fadeOut(0.5f),
                Actions.run(() -> notificationLabel.setVisible(false)) // Hide after fading out
        ));
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
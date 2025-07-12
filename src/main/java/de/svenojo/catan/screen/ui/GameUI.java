package de.svenojo.catan.screen.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.svenojo.catan.world.material.MaterialType;

public class GameUI {

    private Stage stage; 
    private Skin skin;

    private Label currentPlayerLabel;
    private Label rolledNumberLabel;

    public GameUI() {
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true);

        table.top().left().pad(10);
        // Current Player Label
        currentPlayerLabel = new Label("Spieler: ", skin);
        currentPlayerLabel.setFontScale(1.5f);

        table.add(currentPlayerLabel)
            .width(500)
            .left().row();

        // Rolled Number Label
        rolledNumberLabel = new Label("Geworfene Zahl: ", skin);
        rolledNumberLabel.setFontScale(2.0f);
        
        table.add(rolledNumberLabel)
            .expandX()
            .center()
            .row();

        // MAterial Labels
        List<MaterialType> materialTypes = List.of(MaterialType.actualMaterialValues());
        initializeMaterials(materialTypes, table);
    }

    public Stage getStage() {
        return stage;
    }

    public void updateCurrentPlayer(String currentPlayerDisplay) {
        currentPlayerLabel.setText("Spieler: " + currentPlayerDisplay);
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
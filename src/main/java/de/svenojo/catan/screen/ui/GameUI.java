package de.svenojo.catan.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameUI {

    private Stage stage; 
    private Skin skin;

    private Label currentPlayerLabel;

    public GameUI() {
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("data/ui/flat-earth/skin/flat-earth-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true);

        currentPlayerLabel = new Label("Aktueller Spieler: ", skin);
        currentPlayerLabel.setFontScale(2.0f);
        
        table.top().left().pad(10);
        table.add(currentPlayerLabel)
            .width(500)
            .height(200)
            .left().row();
    }

    public Stage getStage() {
        return stage;
    }

    public void updateCurrentPlayer(String currentPlayerDisplay) {
        currentPlayerLabel.setText("Aktueller Speiler: " + currentPlayerDisplay);
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
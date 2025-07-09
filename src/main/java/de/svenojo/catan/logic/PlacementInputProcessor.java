package de.svenojo.catan.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import de.svenojo.catan.world.WorldMap;

public class PlacementInputProcessor extends InputAdapter {
    private CatanGameLogic catanGameLogic;
    private WorldMap worldMap;

    public PlacementInputProcessor(CatanGameLogic catanGameLogic, WorldMap worldMap) {
        this.catanGameLogic = catanGameLogic;
        this.worldMap = worldMap;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("DEBUG","PlacementInputProcessor.touchDown() called with button: " + button);
        if (button == Input.Buttons.LEFT) {
            if (!catanGameLogic.isPlayerPlacingBuilding())
                return false;
            if (!worldMap.isSomethingHighlighted())
                return false;
            catanGameLogic.onBuildingTouchDown();
            return true;
        }
        return false;
    }

}

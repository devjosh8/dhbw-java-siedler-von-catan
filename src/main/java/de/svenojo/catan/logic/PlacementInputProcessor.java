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
        //Gdx.app.log("DEBUG","PlacementInputProcessor.touchDown() called with button: " + button);
        if (button == Input.Buttons.LEFT) {
            if (!catanGameLogic.isPlayerPlacingBuilding() && !catanGameLogic.isPlayerPlacingRobber() && !catanGameLogic.isPlayerTradingWithHarbour()) {
                return false;
            }
            if (!worldMap.isSomethingHighlighted()) {
                Gdx.app.log("DEBUG", "Nothing is highlighted");
                return false;
            }
            Gdx.app.log("DEBUG", "Currently Highlighted Type is " + worldMap.getHighlightingType());
            catanGameLogic.onMouseTouchDown();
            return true;
        }
        return false;
    }

}

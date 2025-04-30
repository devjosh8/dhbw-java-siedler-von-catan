package de.svenojo.catan.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;

import de.svenojo.catan.world.TileType;
import de.svenojo.catan.world.building.BuildingType;

public class CatanAssetManager {
    
    private AssetManager assetManager;

    public BitmapFont worldMapIslandNumberFont;

    public CatanAssetManager() {
        assetManager = new AssetManager();
    }
    
    /**
     * Initialisiert alle Assets.
     * Hier müssen alle Assets eingetragen werden, damit sie danach zur Laufzeit abgefragt werden können
     * Außnahme: Assets, die erst zur Laufzeit geladen werden sollen
     */
    public void initializeAssets() {

        // Modelle für die Map initialisieren
        for(TileType type : TileType.values()) {
            loadModel(type.getFileName());
        }

        // Modelle für Gebäude initialisieren
        for(BuildingType type : BuildingType.values()) {
            loadModel(type.getFileName());
        }

        FreeTypeFontGenerator robotoGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/Roboto-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter robotoParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        robotoParameter.size = 48;
        robotoParameter.color = Color.WHITE;

        robotoParameter.borderWidth = 2f; 
        robotoParameter.borderColor = Color.BLACK; 
        robotoParameter.borderStraight = false;
        worldMapIslandNumberFont = robotoGenerator.generateFont(robotoParameter);
        robotoGenerator.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    /** @param fileName Dateipfad der Datei
	 * @return Model
	 */
	public synchronized Model getModel(String fileName) {
		return assetManager.get(fileName, Model.class);
	}

    /** @param fileName Dateipfad der Datei
	 */
	public synchronized void loadModel(String fileName) {
		assetManager.load(fileName, Model.class);
	}
}

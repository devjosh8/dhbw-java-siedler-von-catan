package de.svenojo.catan.resources;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;

import de.svenojo.catan.world.TileType;

public class CatanAssetManager {
    
    private AssetManager assetManager;

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

package de.svenojo.catan.resources;

import com.badlogic.gdx.assets.AssetManager;

public class CatanAssetManager {
    
    private AssetManager assetManager;

    public CatanAssetManager() {
        assetManager = new AssetManager();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}

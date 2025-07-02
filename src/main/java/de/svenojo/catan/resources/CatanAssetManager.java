package de.svenojo.catan.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.svenojo.catan.core.Globals;
import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.tile.TileType;

public class CatanAssetManager {
    
    private AssetManager assetManager;

    public BitmapFont worldMapIslandNumberFont;
    public BitmapFont mainFontWithoutBorder;
    public BitmapFont mainFontWithBorder;

    public ShaderProgram waterShader;


    public Texture waterTexture;
    public Texture waterOffsetTexture;
    public Texture waterNormalTexture;

    public Sound clickSound;

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
        robotoParameter.size = Globals.IS_MACOS ? 48 : 24;
        robotoParameter.color = Color.WHITE;

        robotoParameter.borderWidth = 2f; 
        robotoParameter.borderColor = Color.BLACK; 
        robotoParameter.borderStraight = false;
        worldMapIslandNumberFont = robotoGenerator.generateFont(robotoParameter);
        robotoGenerator.dispose();


        waterShader = new ShaderProgram(
            Gdx.files.internal("data/shaders/water_vertex.glsl"),
            Gdx.files.internal("data/shaders/water_fragment.glsl")
        );
        if(!waterShader.isCompiled()) {
            throw new GdxRuntimeException("Shader compilation error: " + waterShader.getLog());
        }

        waterTexture = new Texture(Gdx.files.internal("data/textures/water/water_diffuse.png"));
        waterTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        waterOffsetTexture = new Texture(Gdx.files.internal("data/textures/water/water_uv_offset.png"));
        waterOffsetTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        waterNormalTexture = new Texture(Gdx.files.internal("data/textures/water/water_normal.png"));
        waterNormalTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        
        initializeAssetsForMainMenu();
        initializeAssetsForCreditMenu();
        initializeSounds();
    }

    private void initializeAssetsForCreditMenu() {
        FreeTypeFontGenerator robotoThinGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/Roboto-Thin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter robotoThinParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        robotoThinParameter.size =  Globals.IS_MACOS ? 48 : 24;
        robotoThinParameter.color = Color.WHITE;

        mainFontWithoutBorder = robotoThinGenerator.generateFont(robotoThinParameter);
        robotoThinGenerator.dispose();
    }

    private void initializeAssetsForMainMenu() {
        FreeTypeFontGenerator robotoThinGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/Roboto-SemiBold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter robotoThinParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        robotoThinParameter.size = Globals.IS_MACOS ? 40 : 20;
        robotoThinParameter.color = Color.WHITE;
        robotoThinParameter.shadowOffsetX = 2;
        robotoThinParameter.shadowOffsetY = 2;
        robotoThinParameter.shadowColor = new Color(0, 0, 0, 0.75f);

        mainFontWithBorder = robotoThinGenerator.generateFont(robotoThinParameter);
        robotoThinGenerator.dispose();
    }

    private void initializeSounds() {
        loadSound("data/sounds/click_sound.wav");
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
	 * @return Model
	 */
	public synchronized Sound getSound(String fileName) {
		return assetManager.get(fileName, Sound.class);
	}

    /** @param fileName Dateipfad der Datei
	 */
	public synchronized void loadSound(String fileName) {
		assetManager.load(fileName, Sound.class);
	}

    /** @param fileName Dateipfad der Datei
	 */
	public synchronized void loadModel(String fileName) {
		assetManager.load(fileName, Model.class);
	}
}

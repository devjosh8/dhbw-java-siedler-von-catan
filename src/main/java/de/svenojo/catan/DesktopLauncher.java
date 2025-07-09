package de.svenojo.catan;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

import de.svenojo.catan.core.Globals;
import de.svenojo.catan.core.CatanGame;

public class DesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setIdleFPS(Globals.FPS);
        configuration.useVsync(Globals.USE_VSYNC);
        if(isMacOS()) {
            Globals.IS_MACOS = true;
            configuration.setWindowedMode(Globals.GAME_WIDTH/2, Globals.GAME_HEIGHT/2);
        } else {
             //configuration.setHdpiMode(HdpiMode.Logical);
             configuration.setWindowedMode(Globals.GAME_WIDTH, Globals.GAME_HEIGHT);
             System.out.println("meddl");
        }
        configuration.setTitle(Globals.GAME_TITLE);
        configuration.setWindowIcon("data/icons/window_icon48x48.png");
        configuration.setResizable(false);
        configuration.setHdpiMode(HdpiMode.Pixels);
        new Lwjgl3Application(new CatanGame(), configuration);
    }

    public static boolean isMacOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }
}
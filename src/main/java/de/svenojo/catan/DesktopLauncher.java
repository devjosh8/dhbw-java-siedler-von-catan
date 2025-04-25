package de.svenojo.catan;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.svenojo.catan.core.Globals;
import de.svenojo.catan.core.Catan;

public class DesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setIdleFPS(Globals.FPS);
        configuration.useVsync(Globals.USE_VSYNC);
        configuration.setTitle(Globals.GAME_TITLE);
        configuration.setWindowedMode(Globals.GAME_WIDTH, Globals.GAME_HEIGHT);
        new Lwjgl3Application(new Catan(), configuration);
    }
}
package de.svenojo.catan.interfaces;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;

public interface IRenderable2D {

    /**
     * Klassen, die das Interface implementieren, erben diese Renderfunktion
     */
    void render2D(SpriteBatch spriteBatch, Environment environment, Camera camera);
}

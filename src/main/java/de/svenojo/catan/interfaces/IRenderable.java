package de.svenojo.catan.interfaces;


import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface IRenderable {
    
    /**
     * Klassen, die das Interface implementieren, erben diese Renderfunktion
     */
    void render(ModelBatch modelBatch, Environment environment);

}

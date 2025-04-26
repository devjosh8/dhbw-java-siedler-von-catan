package de.svenojo.catan.interfaces;


import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface IRenderable {
    
    void render(ModelBatch modelBatch, Environment environment);

}

package de.svenojo.catan.world.bandit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.tile.Tile;

public class Bandit {
    
    private Tile position;

    private ModelInstance banditInstance;

    private CatanAssetManager catanAssetManager;

    public Bandit(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(banditInstance);
    } 

    public Tile getPosition() {
        return position;
    }

    public void setPosition(Tile position) {
        ModelInstance instance = new ModelInstance(catanAssetManager.getModel("data/models/bandit.g3db"));
        instance = changeColor(instance, Color.WHITE);
        Vector3 pos = new Vector3();
        pos.x = position.getWorldPosition().x;
        pos.z = position.getWorldPosition().z;
        pos.y = 0.5f;
        instance.transform.setToTranslation(pos);
        instance.transform.scale(0.004f, 0.004f, 0.004f);
        banditInstance = instance;
        this.position = position;
    }

    private ModelInstance changeColor(ModelInstance instance, Color color) {
        for (com.badlogic.gdx.graphics.g3d.model.Node node : instance.nodes) {
                for (NodePart part : node.parts) {
                    Material mat = part.material;

                    if (mat.has(ColorAttribute.Diffuse)) {
                        ColorAttribute diffuse = (ColorAttribute) mat.get(ColorAttribute.Diffuse);
                        diffuse.color.set(color);
                    } else {
                        mat.set(ColorAttribute.createDiffuse(color));
                    }
                    mat.set(ColorAttribute.createSpecular(Color.WHITE)); // oder z. B. Color.LIGHT_GRAY
                    mat.set(FloatAttribute.createShininess(32f)); // Bereich: 0 (matt) – 128 (glänzend)
                }
            }

            return instance;
    }
}

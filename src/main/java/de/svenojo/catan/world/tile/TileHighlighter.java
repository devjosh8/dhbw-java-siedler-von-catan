package de.svenojo.catan.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Timer;

public class TileHighlighter {

    private static boolean currentlyHighlightedModelInstance = false;


    public static void setModelInstanceHighlightTemporarily(final ModelInstance instance) {
        if (currentlyHighlightedModelInstance) return;
        currentlyHighlightedModelInstance = true;

        final float highlightFactor = 1.5f; // Stärke des Highlights

        // Speicher die Originalfarben
        final Color[] originalColors = new Color[instance.materials.size];
        for (int i = 0; i < instance.materials.size; i++) {
            Material mat = instance.materials.get(i);
            Attribute attr = mat.get(ColorAttribute.Diffuse);
            if (attr != null && attr instanceof ColorAttribute) {
                ColorAttribute colorAttr = (ColorAttribute) attr;
                originalColors[i] = new Color(colorAttr.color);
                
                // Farbe aufhellen (RGB multiplizieren, Alpha bleibt gleich)
                float r = Math.min(colorAttr.color.r * highlightFactor, 1f);
                float g = Math.min(colorAttr.color.g * highlightFactor, 1f);
                float b = Math.min(colorAttr.color.b * highlightFactor, 1f);
                colorAttr.color.set(r, g, b, colorAttr.color.a);
            } else {
                originalColors[i] = null;
                // Falls kein Diffuse, erzeugen wir eine weiße Farbe als Highlight
                mat.set(ColorAttribute.createDiffuse(Color.WHITE));
            }
        }

        // Nach kurzer Zeit zurücksetzen
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                for (int i = 0; i < instance.materials.size; i++) {
                    Material mat = instance.materials.get(i);
                    Attribute attr = mat.get(ColorAttribute.Diffuse);
                    if (attr != null && attr instanceof ColorAttribute) {
                        ColorAttribute diffuse = (ColorAttribute) attr;
                        if (originalColors[i] != null) {
                            diffuse.color.set(originalColors[i]);
                        }
                    }
                }
                currentlyHighlightedModelInstance = false;
            }
        }, 0.03f); // Delay, kannst du anpassen (z.B. 1f für 1 Sekunde)
    }
}

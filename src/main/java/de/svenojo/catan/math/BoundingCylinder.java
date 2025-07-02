package de.svenojo.catan.math;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class BoundingCylinder {

    private final Vector3 start;    // Untere Kappe
    private final Vector3 axis;     // Normierter Achsenvektor
    private final float height;     // Länge entlang der Achse
    private final float radius;     // Zylinder‐Radius

    public BoundingCylinder(Vector3 start, Vector3 end, float radius) {
        this.start  = new Vector3(start);
        this.radius = radius;
        Vector3 diff = new Vector3(end).sub(start);
        this.height = diff.len();
        this.axis   = diff.nor();
    }

    /**
     * Testet mit einem Ray die Intersection -> mathematisch optimiert und performant
     */
    public boolean intersects(Ray ray) {
        float mx = ray.origin.x - start.x;
        float my = ray.origin.y - start.y;
        float mz = ray.origin.z - start.z;


        float dDotA = ray.direction.x * axis.x
                    + ray.direction.y * axis.y
                    + ray.direction.z * axis.z;


        float mDotA = mx * axis.x
                    + my * axis.y
                    + mz * axis.z;


        float dx = ray.direction.x - dDotA * axis.x;
        float dy = ray.direction.y - dDotA * axis.y;
        float dz = ray.direction.z - dDotA * axis.z;


        float mxp = mx - mDotA * axis.x;
        float myp = my - mDotA * axis.y;
        float mzp = mz - mDotA * axis.z;


        float a = dx*dx + dy*dy + dz*dz;
        float b = 2f * (dx*mxp + dy*myp + dz*mzp);
        float c = mxp*mxp + myp*myp + mzp*mzp - radius*radius;

        // Mantel
        float disc = b*b - 4f*a*c;
        if (disc >= 0f && a > 1e-8f) {
            float sqrtD = (float)Math.sqrt(disc);
            float inv2a = 0.5f / a;
            float t1 = (-b - sqrtD) * inv2a;
            float t2 = (-b + sqrtD) * inv2a;


            float y1 = mDotA + t1 * dDotA;
            if (t1 >= 0f && y1 >= 0f && y1 <= height) return true;

            float y2 = mDotA + t2 * dDotA;
            if (t2 >= 0f && y2 >= 0f && y2 <= height) return true;
        }

        if (Math.abs(dDotA) > 1e-6f) {

            float t0 = -mDotA / dDotA;
            if (t0 >= 0f) {

                float cx = mx + t0 * ray.direction.x;
                float cy = my + t0 * ray.direction.y;
                float cz = mz + t0 * ray.direction.z;
                float proj = mDotA + t0 * dDotA;
                if (cx*cx + cy*cy + cz*cz - proj*proj <= radius*radius) return true;
            }

            float t1 = (height - mDotA) / dDotA;
            if (t1 >= 0f) {
                float cx = mx + t1 * ray.direction.x;
                float cy = my + t1 * ray.direction.y;
                float cz = mz + t1 * ray.direction.z;
                float proj = mDotA + t1 * dDotA - height;
                if (cx*cx + cy*cy + cz*cz - proj*proj <= radius*radius) return true;
            }
        }

        return false;
    }

    /**
     * Erstellt eine ModelInstance in Form eines Quaders,
     * der den gleichen "Radius" (halbe Breite/Tiefe) und die gleiche Höhe
     * wie der Zylinder hat, und richtet ihn entlang der Zylinderachse aus.
     *
     * @param modelBuilder Ein wiederverwendbarer ModelBuilder
     * @param material     Material (Farbe/Textur) für das Mesh
     * @return die fertige ModelInstance
     */
    public ModelInstance toModelInstance(ModelBuilder modelBuilder, Material material) {
        // 1) Erzeuge das Box-Model: Breite=2*radius, Höhe=height, Tiefe=2*radius
        Model model = modelBuilder.createBox(
            2f * radius,
            height,
            2f * radius,
            material,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        // 2) Erstelle die Instance und setze sie zurück
        ModelInstance instance = new ModelInstance(model);
        instance.transform.idt();

        // 3) Berechne die Rotations-Quaternion von Y-Achse auf unsere Achse
        Quaternion q = new Quaternion().setFromCross(
            Vector3.Y,     // Ursprungsausrichtung der Box ist entlang +Y
            axis           // Zielrichtung
        );

        // 4) Berechne die Box-Mittelpunktposition: start + axis * (height/2)
        Vector3 center = new Vector3(start).mulAdd(axis, height * 0.5f);

        // 5) Transformation anwenden: erst verschieben, dann rotieren
        instance.transform.translate(center);
        instance.transform.rotate(q);

        return instance;
    }


    public Vector3 getStart() { 
        return new Vector3(start); 
    }

    public Vector3 getAxis()  { 
        return new Vector3(axis); 
    }

    public float getHeight(){ 
        return height; 
    }

    public float getRadius(){ 
        return radius; 
    }

}

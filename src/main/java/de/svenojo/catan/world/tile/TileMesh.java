package de.svenojo.catan.world.tile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.svenojo.catan.math.Triangle;

public class TileMesh {
    

    private List<Triangle> triangleMesh;

    public TileMesh() {
        triangleMesh = new ArrayList<>();
    }

    public List<Triangle> getTriangleMesh() {
        return triangleMesh;
    }

    public void setHexagonTriangles(Vector3 center, float radius) {
        Vector3[] corners = calculateHexCornersXZ(center, radius);
        List<Triangle> triangles = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            triangles.add(new Triangle(center, corners[i], corners[(i + 1) % 6]));
        }

        this.triangleMesh = triangles;
    }

    private Vector3[] calculateHexCornersXZ(Vector3 center, float radius) {
        Vector3[] corners = new Vector3[6];
        for (int i = 0; i < 6; i++) {
            float angleDeg = 60 * i - 30;
            float angleRad = (float) Math.toRadians(angleDeg);
            float x = center.x + radius * (float) Math.cos(angleRad);
            float z = center.z + radius * (float) Math.sin(angleRad);
            corners[i] = new Vector3(x, center.y + 0.17f, z);
        }
        return corners;
    }


    /**
     * 
     * @param ray Der Raycast
     * @param hitPoint Ein leerer Vector3, in den der Ort der Kollission geschrieben wird
     * @return true oder false, ob der Ray dieses TriangleMesh hittet
     */
    public boolean rayIntersectsHex(Ray ray, Vector3 hitPoint) {
        List<Triangle> tris = triangleMesh;
        boolean hit = false;
        float minDist = Float.MAX_VALUE;
        Vector3 tmp = new Vector3();

        for (Triangle tri : tris) {
            if (Intersector.intersectRayTriangle(ray, tri.v0, tri.v1, tri.v2, tmp)) {
                float dist = ray.origin.dst2(tmp);
                if (dist < minDist) {
                    minDist = dist;
                    hitPoint.set(tmp);
                    hit = true;
                }
            }
        }
        return hit;
    }
}

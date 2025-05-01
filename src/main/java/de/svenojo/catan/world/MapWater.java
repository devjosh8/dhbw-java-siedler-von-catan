package de.svenojo.catan.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.svenojo.catan.interfaces.IRenderable;
import de.svenojo.catan.interfaces.ITickable;
import de.svenojo.catan.resources.CatanAssetManager;

public class MapWater implements ITickable, IRenderable {

    private final float WATER_PLANE_SIZE = 100.0f;
    private final float WATER_PLANE_HEIGHT = 0.0f;

    private ModelInstance waterPlaneInstance;
    private Array<Renderable> waterPlaneRenderables = new Array<>();

    private CatanAssetManager catanAssetManager;
    private ShaderProgram waterShader;

    private long startTime;

    public MapWater(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
        startTime = System.currentTimeMillis();
    }

    public void loadAssets() {
        this.waterShader = catanAssetManager.waterShader;

        Model waterPlaneModel = createWaterPlane(WATER_PLANE_SIZE, 1, 80f);
        waterPlaneInstance = new ModelInstance(waterPlaneModel);

        /**
         * Notwendig, um die Renderables aus dem WaterPlaneModelInstance zu extrahieren...
         */
        Pool<Renderable> dummyPool = new Pool<Renderable>() {
            @Override
            protected Renderable newObject() {
                return new Renderable();
            }
        };
        waterPlaneInstance.getRenderables(waterPlaneRenderables, dummyPool);
    }   

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {

        // Damit das Wasser nicht transparent ist
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glDepthMask(true); 
        Gdx.gl.glDisable(GL20.GL_BLEND);

        waterShader.bind();
        catanAssetManager.waterTexture.bind(0);
        catanAssetManager.waterOffsetTexture.bind(1);
        catanAssetManager.waterNormalTexture.bind(2);

        waterShader.setUniformMatrix("u_projViewTrans", modelBatch.getCamera().combined);
        waterShader.setUniformf("u_time", (startTime-System.currentTimeMillis()) / 1000.0f);
        waterShader.setUniformi("u_textureWater", 0);
        waterShader.setUniformi("u_textureOffset", 1);
        waterShader.setUniformi("u_normalTexture", 2);

        waterShader.setUniformf("u_lightDirection", new Vector3(-0.3f, -1f, -0.2f).nor());
        waterShader.setUniformf("u_lightColor", 1f, 1f, 1f);
        waterShader.setUniformf("u_ambientColor", 0.1f, 0.2f, 0.3f);

        /**
         * Normal Matrix als Invers Transponierte an den Shader übergeben für richtige Licht Berechnung
         */
        Matrix3 normalMatrix = new Matrix3();
        normalMatrix.set(waterPlaneInstance.transform);
        normalMatrix.inv().transpose();
        waterShader.setUniformMatrix("u_normalMatrix", normalMatrix);
        



        for (Renderable renderable : waterPlaneRenderables) {
            waterShader.setUniformMatrix("u_worldTrans", renderable.worldTransform);
            renderable.meshPart.mesh.render(
                waterShader,
                renderable.meshPart.primitiveType,
                renderable.meshPart.offset,
                renderable.meshPart.size
            );
        }

        // für späteres Rendering wieder aktivieren
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    @Override
    public void tick(float delta) {}
    
    /**
     * Gibt ein Model einer Fläche zurück
     * @param size Größe der Fläche
     * @param subdivisions Subdivisionen pro Seite
     * @param textureRepeat Wie oft wird die Textur wiederholt über die Fläche
     * @return das Model
     */
    private Model createWaterPlane(float size, int subdivisions, float textureRepeat) {
        ModelBuilder modelBuilder = new ModelBuilder();
        MeshPartBuilder builder;

        modelBuilder.begin();

        builder = modelBuilder.part("waterPlane", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)));

        float halfSize = size / 2f;
        float step = size / subdivisions;
        float texStep = textureRepeat / subdivisions;

        for (int x = 0; x < subdivisions; x++) {
            for (int z = 0; z < subdivisions; z++) {
                float x0 = -halfSize + x * step;
                float z0 = -halfSize + z * step;
                float x1 = x0 + step;
                float z1 = z0 + step;

                float u0 = x * texStep;
                float v0 = z * texStep;
                float u1 = u0 + texStep;
                float v1 = v0 + texStep;

                Vector3 normal = new Vector3(0f, 1f, 0f);

                VertexInfo v00 = new VertexInfo().setPos(x0, WATER_PLANE_HEIGHT, z0).setNor(normal).setUV(u0, v0);
                VertexInfo v10 = new VertexInfo().setPos(x1, WATER_PLANE_HEIGHT, z0).setNor(normal).setUV(u1, v0);
                VertexInfo v11 = new VertexInfo().setPos(x1, WATER_PLANE_HEIGHT, z1).setNor(normal).setUV(u1, v1);
                VertexInfo v01 = new VertexInfo().setPos(x0, WATER_PLANE_HEIGHT, z1).setNor(normal).setUV(u0, v1);

                builder.triangle(v00, v10, v11);
                builder.triangle(v00, v11, v01);

            }
        }

        return modelBuilder.end();
    }
}

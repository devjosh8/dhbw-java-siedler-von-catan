package de.svenojo.catan.world.building;

import java.util.Random;

import org.jgrapht.Graph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.Node;
import de.svenojo.catan.world.building.buildings.BuildingCity;
import de.svenojo.catan.world.building.buildings.BuildingHarbour;
import de.svenojo.catan.world.building.buildings.BuildingSettlement;
import de.svenojo.catan.world.building.buildings.BuildingStreet;

public class BuildingCalculator {
    
    private CatanAssetManager catanAssetManager;

    public BuildingCalculator(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
    }

    public ModelInstance calculateHarbourModelInstance(BuildingHarbour building, Graph<Node, Edge> nodeGraph) {
        ModelInstance instance = new ModelInstance(catanAssetManager.getModel(building.getBuildingType().getFileName()));
        
        Vector3 position = new Vector3();
        instance = changeColor(instance, Color.valueOf("#2596be"));
        NodeBuilding nodeBuilding = (NodeBuilding) building;

        position.x = nodeBuilding.getPosition().getPosition().x;
        position.z = nodeBuilding.getPosition().getPosition().z;

        position.y = 0.07f;
        instance.transform.setToTranslation(position);

        float angle = (float) (Math.atan2( position.x, position.z) * 360/(2 * Math.PI));

        angle+=90;

        instance.transform.rotate(new Vector3(0, 1.0f, 0f), angle);
        instance.transform.scale(0.01f, 0.01f, 0.01f);
        building.getPosition().setHasHarbour(true);
        return instance;

    }

    public ModelInstance calculateBuildingModelInstance(Player player, Building building, Graph<Node, Edge> nodeGraph) {
        if(building.getBuildingType() == BuildingType.STREET && building instanceof BuildingStreet) {
            
            ModelInstance instance = new ModelInstance(catanAssetManager.getModel(building.getBuildingType().getFileName()));
            instance = changeColor(instance, player.getColor());

            BuildingStreet buildingStreet = (BuildingStreet) building;

            Vector3 position = new Vector3();
            Node source = nodeGraph.getEdgeSource(buildingStreet.getPosition());
            Node target = nodeGraph.getEdgeTarget(buildingStreet.getPosition());

            float delta_x = target.getPosition().x - source.getPosition().x;
            float delta_z = target.getPosition().z - source.getPosition().z;
            
            double theta = Math.tan((double) delta_x / delta_z) * 9.2d; //??? Warum mal 10?? funktioniert aber ._. wtf

            position.x = source.getPosition().x + (delta_x) / 2;
            position.z = source.getPosition().z + (delta_z) / 2;
            position.y = 0.1f;
            instance.transform.setToTranslation(position);
            instance.transform.rotate(new Vector3(0, 1.0f, 0f), (float) (-theta));
            instance.transform.scale(0.006f, 0.008f, 0.008f);
            return instance;
        } else if(building instanceof NodeBuilding) {

            ModelInstance instance = new ModelInstance(catanAssetManager.getModel(building.getBuildingType().getFileName()));

            instance = changeColor(instance, player.getColor());
            
            Vector3 position = new Vector3();
            
            NodeBuilding nodeBuilding = (NodeBuilding) building;

            position.x = nodeBuilding.getPosition().getPosition().x;
            position.z = nodeBuilding.getPosition().getPosition().z;

            if(building instanceof BuildingSettlement) {
                position.y = 0.16f;
                instance.transform.setToTranslation(position);
                instance.transform.rotate(new Vector3(0, 1.0f, 0f), (float) new Random().nextFloat() * 360.0f);
                instance.transform.scale(0.0045f, 0.0045f, 0.0045f);
                return instance;
            } else if(building instanceof BuildingCity) {
                position.y = 0.16f;
                instance.transform.setToTranslation(position);
                instance.transform.rotate(new Vector3(0, 1.0f, 0f), (float) new Random().nextFloat() * 360.0f);
                instance.transform.scale(0.0045f, 0.0045f, 0.0045f);
                return instance;
            }
        }

        return null;
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
                    mat.set(ColorAttribute.createSpecular(0f, 0f, 0f, 1f));
                }
            }

            return instance;
    }
}

package de.svenojo.catan.world.building;

import java.util.Random;

import org.jgrapht.Graph;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import de.svenojo.catan.player.Player;
import de.svenojo.catan.resources.CatanAssetManager;
import de.svenojo.catan.world.Edge;
import de.svenojo.catan.world.Node;
import de.svenojo.catan.world.building.buildings.BuildingCity;
import de.svenojo.catan.world.building.buildings.BuildingSettlement;
import de.svenojo.catan.world.building.buildings.BuildingStreet;

public class BuildingCalculator {
    
    private CatanAssetManager catanAssetManager;

    public BuildingCalculator(CatanAssetManager catanAssetManager) {
        this.catanAssetManager = catanAssetManager;
    }

    public ModelInstance calculateBuildingModelInstance(Player player, Building building, Graph<Node, Edge> nodeGraph) {
        if(building.getBuildingType() == BuildingType.STREET && building instanceof BuildingStreet) {
            
            ModelInstance instance = new ModelInstance(catanAssetManager.getModel(building.getBuildingType().getFileName()));
            

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
            instance.transform.scale(0.011f, 0.011f, 0.014f);
            return instance;
        } else if(building instanceof NodeBuilding) {

            ModelInstance instance = new ModelInstance(catanAssetManager.getModel(building.getBuildingType().getFileName()));

            Vector3 position = new Vector3();
            
            NodeBuilding nodeBuilding = (NodeBuilding) building;

            position.x = nodeBuilding.getPosition().getPosition().x;
            position.z = nodeBuilding.getPosition().getPosition().z;

            if(building instanceof BuildingSettlement) {
                position.y = 0.16f;
                instance.transform.setToTranslation(position);
                instance.transform.rotate(new Vector3(0, 1.0f, 0f), (float) new Random().nextFloat() * 30.0f);
                instance.transform.scale(0.009f, 0.009f, 0.009f);
                return instance;
            } else if(building instanceof BuildingCity) {
                /**
                 * Stadt platzieren
                 */
            }
        }

        return null;
    }
}

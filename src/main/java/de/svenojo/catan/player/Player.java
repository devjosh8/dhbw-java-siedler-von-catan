package de.svenojo.catan.player;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.svenojo.catan.world.building.BuildingType;
import de.svenojo.catan.world.material.MaterialType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {    
    private Color color;
    private String name;

    private int streetAmount;
    private int settlementAmount;
    private int cityAmount;

    private MaterialContainer materialContainer;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;

        streetAmount = 0;
        settlementAmount = 0;
        cityAmount = 0;

        materialContainer = new MaterialContainer();
    }
    public String getName() {
        return name;
    }
    public String getColorString() {
         if (color.equals(Color.RED)) {
            return "Red";
        } else if (color.equals(Color.BLUE)) {
            return "Blue";
        } else if (color.equals(Color.GREEN)) {
            return "Green";
        } else if (color.equals(Color.YELLOW)) {
            return "Yellow";
        } else if (color.equals(Color.WHITE)) {
            return "White";
        } else if (color.equals(Color.BLACK)) {
            return "Black";
        } else if (color.equals(Color.ORANGE)) {
            return "Orange";
        } else if (color.equals(Color.PINK)) {
            return "Pink";
        } else if (color.equals(Color.PURPLE)) {
            return "Purple";
        } else if (color.equals(Color.CYAN)) {
            return "Cyan";
        } else if (color.equals(Color.GRAY)) {
            return "Gray";
        } else {
            return "Unknown Color";
        }
    }

    public boolean canAfford(BuildingType type) {
        return materialContainer.canAfford(type);
    }

    public void removeMaterialForBuilding(BuildingType type) {
        materialContainer.removeMaterialForBuilding(type);
    }

    public int getTotalMaterialCount() {
        return materialContainer.getTotalMaterialCount();
    }

    public void addMaterial(MaterialType type, int amount) {
        switch (type) {
            case MaterialType.WOOD:
                getMaterialContainer().setWoodMaterialCount(getMaterialContainer().getWoodMaterialCount() + amount);
                break;
            case MaterialType.WHEAT:
                getMaterialContainer().setWheatMaterialCount(getMaterialContainer().getWheatMaterialCount() + amount);
                break;
            case MaterialType.WOOL:
                getMaterialContainer().setWoolMaterialCount(getMaterialContainer().getWoolMaterialCount() + amount);
                break;
            case MaterialType.CLAY:
                getMaterialContainer().setClayMaterialCount(getMaterialContainer().getClayMaterialCount() + amount);
                break;
            case MaterialType.ORE:
                getMaterialContainer().setOreMaterialCount(getMaterialContainer().getOreMaterialCount() + amount);
                break;
            case MaterialType.NONE:
                break;
        }
    }

    public int getMaterialCount(MaterialType type) {
        switch (type) {
            case MaterialType.WOOD:
                return getMaterialContainer().getWoodMaterialCount();
        
            case MaterialType.WHEAT:
                return getMaterialContainer().getWheatMaterialCount();
            
            case MaterialType.WOOL:
                return getMaterialContainer().getWoolMaterialCount();
           
            case MaterialType.CLAY:
                return getMaterialContainer().getClayMaterialCount();

            case MaterialType.ORE:
                return getMaterialContainer().getOreMaterialCount();

            case MaterialType.NONE:
                return 0;
        }

        return -99999;
    }

    public HashMap<MaterialType, Integer> getMaterials() {
        HashMap<MaterialType, Integer> materials = new HashMap<>();
        materials.put(MaterialType.WOOD, getMaterialCount(MaterialType.WOOD));
        materials.put(MaterialType.WHEAT, getMaterialCount(MaterialType.WHEAT));
        materials.put(MaterialType.WOOL, getMaterialCount(MaterialType.WOOL));
        materials.put(MaterialType.CLAY, getMaterialCount(MaterialType.CLAY));
        materials.put(MaterialType.ORE, getMaterialCount(MaterialType.ORE));
        Gdx.app.log("DEBUG", "Player " + name + " materials: " + materials.toString());
        return materials;
    }


    public int getScore() {
        return settlementAmount + (cityAmount * 2);
    }

    public boolean hasWon() {
        return getScore() >= 10;
    }
}

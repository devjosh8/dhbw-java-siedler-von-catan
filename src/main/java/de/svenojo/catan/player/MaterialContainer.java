package de.svenojo.catan.player;

import de.svenojo.catan.world.building.BuildingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class MaterialContainer {
    
    private int woodMaterialCount = 0;
    private int oreMaterialCount = 0;
    private int wheatMaterialCount = 0;
    private int woolMaterialCount = 0;
    private int clayMaterialCount = 0;



    public int getTotalMaterialCount() {
        return woodMaterialCount + oreMaterialCount + wheatMaterialCount + woolMaterialCount + clayMaterialCount;
    }

    public boolean canAfford(BuildingType type) {
        switch (type) {
            case STREET:
                return clayMaterialCount>=1 && woodMaterialCount >=1;
            case SETTLEMENT:
                return clayMaterialCount>=1 && woodMaterialCount>=1 && woolMaterialCount>=1 && wheatMaterialCount>=1;
            case CITY:
                return wheatMaterialCount>=2 && oreMaterialCount>=3;
            default:
                return false;
        }
    }

    public void removeMaterialForBuilding(BuildingType type) {
        switch (type) {
            case STREET:
                clayMaterialCount--;
                woodMaterialCount--;
                break;
            case SETTLEMENT:
                clayMaterialCount--;
                woodMaterialCount--;
                woolMaterialCount--;
                wheatMaterialCount--;
                break;
            case CITY:
                wheatMaterialCount -= 2;
                oreMaterialCount -= 3;
                break;
            default:
                throw new IllegalArgumentException("Unknown building type: " + type);
        }
    }
}

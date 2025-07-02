package de.svenojo.catan.world;

import java.util.Optional;

import de.svenojo.catan.math.BoundingCylinder;
import de.svenojo.catan.world.building.buildings.BuildingStreet;

public class Edge {
    
    private final float BOUNDING_CYLINDER_RADIUS = 0.3f;

    private Optional<BuildingStreet> street;

    private Node source, target;

    private BoundingCylinder boundingCylinder;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
        generateBoundingBox();
    }

    public void setStreet(BuildingStreet street) {
        this.street = Optional.of(street);
    }

    public boolean hasStreet() {
        return street.isPresent();
    }

    public BuildingStreet getStreet() {
        if(!street.isPresent()) return null;
        return street.get();
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    private void generateBoundingBox() {
        boundingCylinder = new BoundingCylinder(target.getPosition(), source.getPosition(), BOUNDING_CYLINDER_RADIUS);
    }

    public BoundingCylinder getBoundingCylinder() {
        return boundingCylinder;
    }
}

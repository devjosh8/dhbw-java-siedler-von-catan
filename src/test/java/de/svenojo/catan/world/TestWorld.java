package de.svenojo.catan.world;

import org.junit.jupiter.api.Test;

import de.svenojo.catan.math.AxialVector;

import static org.junit.jupiter.api.Assertions.*;

public class TestWorld {
    
    @Test
    public void testAxialVectors() {
        AxialVector a = new AxialVector(1, 1);
        AxialVector b = new AxialVector(1, 1);
        AxialVector c = new AxialVector(1, 2);

        assertTrue(a.equals(b));
        assertFalse(a.equals(c));
        assertFalse(b.equals(c));
        assertTrue(c.equals(c));
    }
}

package de.svenojo.catan.interfaces;

public interface ITickable {
    
    /**
     * Klassen, die das Interface implementieren, erben diese Tickfunktion
     * @param delta Die vergangene Zeit des letzten gerenderten Frames
     */
    public void tick(float delta);
}

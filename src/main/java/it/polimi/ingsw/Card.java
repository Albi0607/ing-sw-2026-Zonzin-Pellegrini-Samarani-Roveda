package it.polimi.ingsw;

public abstract class Card {

    private Era era;

    public Era getEra() { return null; }

    /** Buildings remain in row across rounds; characters/events do not. */
    public abstract boolean isPersistent();

    public abstract boolean isBuilding();
}
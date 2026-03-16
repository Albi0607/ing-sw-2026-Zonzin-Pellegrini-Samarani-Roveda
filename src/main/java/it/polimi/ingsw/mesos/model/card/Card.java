package it.polimi.ingsw.mesos.model.card;

import it.polimi.ingsw.mesos.model.enums.Era;

public abstract class Card {

    private Era era;

    public Card(Era era) { }
// non presente in UML
    public Era getEra() { return null; }

    /** Buildings remain in row across rounds; characters/events do not. */
    public abstract boolean isPersistent();

    public abstract boolean isBuilding();
}

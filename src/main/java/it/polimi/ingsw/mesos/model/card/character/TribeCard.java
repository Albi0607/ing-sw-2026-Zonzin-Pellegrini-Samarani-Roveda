package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.enums.Era;

public abstract class TribeCard extends Card {

    private int playersRequired;

    public TribeCard(Era era, int playersRequired) {
        super(era);
    }

    @Override
    public boolean isPersistent() { return false; }

    @Override
    public boolean isBuilding() { return false; }

    public int getPlayersRequired() { return 0; }
}

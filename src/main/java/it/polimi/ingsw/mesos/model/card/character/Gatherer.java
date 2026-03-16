package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Gatherer extends CharacterCard {

    private final int foodDiscount = 3;

    public Gatherer(Era era, int playersRequired) {
        super(era, playersRequired, CharacterType.GATHERER);
    }
}

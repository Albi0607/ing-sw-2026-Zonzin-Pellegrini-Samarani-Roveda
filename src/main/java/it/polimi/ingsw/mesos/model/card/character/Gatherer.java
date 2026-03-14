package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Gatherer extends CharacterCard {

    /** Food discount provided during Sustenance event (always 3 per card). */
    private final int foodDiscount = 3;

    public Gatherer(Era era, int playersRequired) {
        super(era, playersRequired, CharacterType.GATHERER);
    }

    public int getFoodDiscount() { return 0; }
}

package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Builder extends CharacterCard {

    /** Food cost reduction applied to each Building purchase. */
    private int discountValue;

    /** Prestige points awarded at end of game. */
    private int prestigePoints;

    public Builder(Era era, int playersRequired, int discountValue, int prestigePoints) {
        super(era, playersRequired, CharacterType.BUILDER);
    }

    public int getDiscountValue() { return 0; }

    public int getPrestigePoints() { return 0; }
}

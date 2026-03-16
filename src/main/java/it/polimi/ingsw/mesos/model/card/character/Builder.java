package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Builder extends CharacterCard {

    private int discountValue;
    private int prestigePoints;

    public Builder(Era era, int playersRequired, int discountValue, int prestigePoints) {
        super(era, playersRequired, CharacterType.BUILDER);
    }
}

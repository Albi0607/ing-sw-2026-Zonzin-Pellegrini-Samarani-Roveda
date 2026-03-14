package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Shaman extends CharacterCard {

    /** Number of star icons on this card (1 to 3). */
    private int numberOfIcons;

    public Shaman(Era era, int playersRequired, int numberOfIcons) {
        super(era, playersRequired, CharacterType.SHAMAN);
    }

    public int getNumberOfIcons() { return 0; }
}

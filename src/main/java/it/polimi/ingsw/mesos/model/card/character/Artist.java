package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Artist extends CharacterCard {

    public Artist(Era era, int playersRequired) {
        super(era, playersRequired, CharacterType.ARTIST);
    }
}

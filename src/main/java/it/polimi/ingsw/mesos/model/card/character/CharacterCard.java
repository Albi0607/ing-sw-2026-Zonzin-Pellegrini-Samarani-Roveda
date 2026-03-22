package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public abstract class CharacterCard extends TribeCard {

    public CharacterType type;

    public CharacterCard(Era era, int playersRequired, CharacterType type) {
        super(era, playersRequired);
    }

    public CharacterType getType() {
        return type;
    }
}

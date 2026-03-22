package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.InventionIcon;

public class Inventor extends CharacterCard {

    private InventionIcon icon;

    public Inventor(Era era, int playersRequired, InventionIcon icon) {
        super(era, playersRequired, CharacterType.INVENTOR);
    }

    public InventionIcon getIcon() {
        return icon;
    }
}

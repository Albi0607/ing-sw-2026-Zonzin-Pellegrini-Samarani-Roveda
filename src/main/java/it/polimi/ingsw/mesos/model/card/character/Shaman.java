package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Shaman extends CharacterCard {

    // ho tolto il metodo getter, ma magari getter di questo tipo possono essere utili
    // per velocizzare la scrittura delle implementazioni e rendere più leggibile il codice
    private int numberOfIcons;

    public Shaman(Era era, int playersRequired, int numberOfIcons) {
        super(era, playersRequired, CharacterType.SHAMAN);
    }
}

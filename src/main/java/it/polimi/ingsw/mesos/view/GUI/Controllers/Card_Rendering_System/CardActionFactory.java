package it.polimi.ingsw.mesos.view.GUI.Controllers.Card_Rendering_System;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;

public class CardActionFactory {

    public static CardAction create(CardDTO card) {

        String id = card.id;

        if (id.startsWith("EV")) {
            return new EventCardAction();
        }
        else if (id.startsWith("CH")) {
            return new CharacterCardAction();
        }
        else if (id.startsWith("BD")) {
            return new BuildingCardAction();
        }

        return null;
    }

}

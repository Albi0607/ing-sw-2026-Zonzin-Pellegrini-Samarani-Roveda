package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.common.BuildingCardJson;
import it.polimi.ingsw.mesos.common.CharacterCardJson;
import it.polimi.ingsw.mesos.common.EventCardJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class CardJson {
    @Test
    void getId(){
        EventCardJson event = new EventCardJson();
        CharacterCardJson character = new CharacterCardJson();
        BuildingCardJson building = new BuildingCardJson();
        assertNull(event.getId());
        assertNull(character.getId());
        assertNull(building.getId());
    }
}

package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventCardTest {

    @Test
    void testGetType() {
        ShamanicRitualEvent shamanicRitualEvent= new ShamanicRitualEvent(Era.ERA_III,4,true,15,7);
        assertEquals(EventType.SHAMAN_RITUAL,shamanicRitualEvent.getType());
    }

    @Test
    void isFinal() {
        ShamanicRitualEvent shamanicRitualEvent1= new ShamanicRitualEvent(Era.ERA_III,4,true,15,7);
        ShamanicRitualEvent shamanicRitualEvent2= new ShamanicRitualEvent(Era.ERA_II,4,false,10,5);
        assertTrue(shamanicRitualEvent1.isFinal());
        assertFalse(shamanicRitualEvent2.isFinal());
    }
}
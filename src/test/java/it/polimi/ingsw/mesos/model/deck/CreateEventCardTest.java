package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEventCardTest {

    @Test
    void testCorrectNumberOfEvent(){
        List<EventCard> list = new CreateEventCard().getAllEventCards();
        assertEquals(12,list.size());
    }

}
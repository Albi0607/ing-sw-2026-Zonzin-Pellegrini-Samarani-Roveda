package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.event.EventCard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateEventCardTest {

    @Test
    void testCorrectNumberOfEvent(){
        List<EventCard> list = new CreateEventCard("cards/events.json").getAllEventCards();
        assertEquals(12,list.size());
    }

    @Test
    void testWrongInputFromJson(){
        assertThrows(RuntimeException.class,()->new CreateEventCard("cards/wrongEventsForTest.json"));
    }

    @Test
    void testWrongFile(){
        assertThrows(RuntimeException.class,()->new CreateEventCard("prova.json"));
    }
}
package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.Artist;
import it.polimi.ingsw.mesos.model.deck.CreateEventCard;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CavePaintingEventTest {

    @Test
    void testAllCavePaintingEventCardsWithCreateEventCard() {
        CavePaintingEvent card1=null;
        CavePaintingEvent card2=null;
        CavePaintingEvent card3=null;
        List<EventCard> deck = new CreateEventCard("events.json").getAllEventCards();
        for (EventCard c : deck) {
            if (c instanceof CavePaintingEvent && c.getEra()== Era.ERA_I) {
                card1 = (CavePaintingEvent) c;
            }
            if (c instanceof CavePaintingEvent && c.getEra()== Era.ERA_II) {
                card2 = (CavePaintingEvent) c;
            }
            if (c instanceof CavePaintingEvent&& c.getEra()== Era.ERA_III) {
                card3 = (CavePaintingEvent) c;
            }
        }
        assertEquals(2,card1.getPlayerRequired());
        assertFalse(card1.isFinal());

        //era 2
        assertEquals(2,card2.getPlayerRequired());
        assertFalse(card2.isFinal());

        //era 3
        assertEquals(2,card3.getPlayerRequired());
        assertFalse(card3.isFinal());
    }


    @Test
    void testNormalResolve() {
        Player p1 = new Player("Alberto", Color.BLUE);
        Player p2 = new Player("Anna", Color.PURPLE);
        Player p3 = new Player("Luca", Color.WHITE);
        Player p4 = new Player("Mattia", Color.RED);
        List<Player> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);

        Game game = new Game(list);

        Artist artist = new Artist(Era.ERA_III,4);

        CavePaintingEvent cavePaintingEvent = new CavePaintingEvent(Era.ERA_II,2,false,2,3,2,3);

        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(artist);

        cavePaintingEvent.resolve(game);

        assertEquals(12,p1.getPrestigePoints());

        assertEquals(9,p2.getPrestigePoints());

        assertEquals(-2,p3.getPrestigePoints());

        assertEquals(-2,p4.getPrestigePoints());
    }
}
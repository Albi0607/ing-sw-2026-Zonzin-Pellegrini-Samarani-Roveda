package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.Hunter;
import it.polimi.ingsw.mesos.model.deck.CreateEventCard;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HuntEventTest {

    @Test
    void testAllHunterEventCardsWithCreateEventCard() {
        HuntEvent card1=null;
        HuntEvent card2=null;
        HuntEvent card3=null;
        List<EventCard> deck = new CreateEventCard("events.json").getAllEventCards();
        for (EventCard c : deck) {
            if (c instanceof HuntEvent && c.getEra()== Era.ERA_I) {
                card1 = (HuntEvent)c;
            }
            if (c instanceof HuntEvent && c.getEra()== Era.ERA_II) {
                card2 = (HuntEvent)c;
            }
            if (c instanceof HuntEvent && c.getEra()== Era.ERA_III) {
                card3 = (HuntEvent)c;
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

        Hunter hunter1 = new Hunter(Era.ERA_I,2,true);
        Hunter hunter2 = new Hunter(Era.ERA_I,2,false);
        Hunter hunter3 = new Hunter(Era.ERA_II,3,true);
        Hunter hunter4 = new Hunter(Era.ERA_II,2,false);
        Hunter hunter5 = new Hunter(Era.ERA_III,4,true);
        Hunter hunter6 = new Hunter(Era.ERA_III,3,false);
        HuntEvent huntEvent = new HuntEvent(Era.ERA_II,2,true,3);

        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter4);
        p1.getTribe().addCharacter(hunter6);
        p1.getTribe().addCharacter(hunter5);
        p2.getTribe().addCharacter(hunter2);
        p2.getTribe().addCharacter(hunter3);
        p3.getTribe().addCharacter(hunter1);

        huntEvent.resolve(game);

        assertEquals(4,p1.getFood());
        assertEquals(12,p1.getPrestigePoints());

        assertEquals(2,p2.getFood());
        assertEquals(6,p2.getPrestigePoints());

        assertEquals(1,p3.getFood());
        assertEquals(3,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }
}
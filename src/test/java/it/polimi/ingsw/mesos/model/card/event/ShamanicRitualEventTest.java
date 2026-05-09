package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.Shaman;
import it.polimi.ingsw.mesos.model.deck.CreateEventCard;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.common.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShamanicRitualEventTest {

    @Test
    void testAllShamanicRitualEventCardsWithCreateEventCard() {
        ShamanicRitualEvent card1=null;
        ShamanicRitualEvent card2=null;
        ShamanicRitualEvent card3=null;
        List<EventCard> deck = new CreateEventCard("cards/events.json").getAllEventCards();
        for (EventCard c : deck) {
            if (c instanceof ShamanicRitualEvent && c.getEra()== Era.ERA_I) {
                card1 = (ShamanicRitualEvent) c;
            }
            if (c instanceof ShamanicRitualEvent && c.getEra()== Era.ERA_II) {
                card2 = (ShamanicRitualEvent) c;
            }
            if (c instanceof ShamanicRitualEvent && c.getEra()== Era.ERA_III) {
                card3 = (ShamanicRitualEvent) c;
            }
        }
        assertEquals(2,card1.getPlayerRequired());
        assertFalse(card1.isFinal());

        //era 2
        assertEquals(2,card2.getPlayerRequired());
        assertFalse(card2.isFinal());

        //era 3
        assertEquals(2,card3.getPlayerRequired());
        assertTrue(card3.isFinal());
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

        Shaman shaman3 = new Shaman(Era.ERA_I,2,3);
        Shaman shaman2 = new Shaman(Era.ERA_I,2,2);
        Shaman shaman1 = new Shaman(Era.ERA_I,2,1);

        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(Era.ERA_II,2,true,15,7);

        p1.getTribe().addCharacter(shaman3);
        p1.getTribe().addCharacter(shaman3);
        p1.getTribe().addCharacter(shaman1);

        p2.getTribe().addCharacter(shaman3);
        p2.getTribe().addCharacter(shaman1);

        p3.getTribe().addCharacter(shaman2);

        p4.getTribe().addCharacter(shaman1);

        shamanicRitualEvent.resolve(game);

        assertEquals(15,p1.getPrestigePoints());

        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getPrestigePoints());

        assertEquals(-7,p4.getPrestigePoints());
    }

    @Test
    void testTwoEqualsResolve() {
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

        Shaman shaman3 = new Shaman(Era.ERA_I,2,3);
        Shaman shaman2 = new Shaman(Era.ERA_I,2,2);
        Shaman shaman1 = new Shaman(Era.ERA_I,2,1);

        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(Era.ERA_II,2,true,15,7);

        p1.getTribe().addCharacter(shaman3);
        p1.getTribe().addCharacter(shaman3);

        p2.getTribe().addCharacter(shaman3);
        p2.getTribe().addCharacter(shaman3);

        p3.getTribe().addCharacter(shaman2);

        p4.getTribe().addCharacter(shaman2);

        shamanicRitualEvent.resolve(game);

        assertEquals(15,p1.getPrestigePoints());

        assertEquals(15,p2.getPrestigePoints());

        assertEquals(-7,p3.getPrestigePoints());

        assertEquals(-7,p4.getPrestigePoints());
    }

    @Test
    void testAllEqualsResolve() {
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

        Shaman shaman3 = new Shaman(Era.ERA_I,2,3);
        Shaman shaman2 = new Shaman(Era.ERA_I,2,2);
        Shaman shaman1 = new Shaman(Era.ERA_I,2,1);

        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(Era.ERA_II,2,true,15,7);

        p1.getTribe().addCharacter(shaman1);

        p2.getTribe().addCharacter(shaman1);

        p3.getTribe().addCharacter(shaman1);

        p4.getTribe().addCharacter(shaman1);

        shamanicRitualEvent.resolve(game);

        assertEquals(8,p1.getPrestigePoints());

        assertEquals(8,p2.getPrestigePoints());

        assertEquals(8,p3.getPrestigePoints());

        assertEquals(8,p4.getPrestigePoints());
    }

    @Test
    void testFlagResolve() {
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

        Shaman shaman3 = new Shaman(Era.ERA_I,2,3);
        Shaman shaman2 = new Shaman(Era.ERA_I,2,2);
        Shaman shaman1 = new Shaman(Era.ERA_I,2,1);

        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(Era.ERA_II,2,true,15,7);

        p1.getTribe().addCharacter(shaman3);
        p1.getTribe().addCharacter(shaman3);
        p1.getTribe().addCharacter(shaman1);
        p1.setShamanDoublePoints();

        p2.getTribe().addCharacter(shaman3);
        p2.getTribe().addCharacter(shaman1);
        p2.setExtraShamanIcons(3);

        p3.getTribe().addCharacter(shaman1);
        p3.setShamanNotLosePoints();

        p4.getTribe().addCharacter(shaman1);

        shamanicRitualEvent.resolve(game);

        assertEquals(30,p1.getPrestigePoints());

        assertEquals(15,p2.getPrestigePoints());

        assertEquals(0,p3.getPrestigePoints());

        assertEquals(-7,p4.getPrestigePoints());
    }

    @Test
    void testAllEqualsWithFlagResolve() {
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

        Shaman shaman3 = new Shaman(Era.ERA_I,2,3);
        Shaman shaman2 = new Shaman(Era.ERA_I,2,2);
        Shaman shaman1 = new Shaman(Era.ERA_I,2,1);

        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(Era.ERA_II,2,true,15,7);

        p1.getTribe().addCharacter(shaman1);
        p1.setShamanDoublePoints();

        p2.getTribe().addCharacter(shaman1);

        p3.getTribe().addCharacter(shaman1);

        p4.getTribe().addCharacter(shaman1);
        p4.setShamanNotLosePoints();

        shamanicRitualEvent.resolve(game);

        assertEquals(23,p1.getPrestigePoints());

        assertEquals(8,p2.getPrestigePoints());

        assertEquals(8,p3.getPrestigePoints());

        assertEquals(15,p4.getPrestigePoints());
    }

}
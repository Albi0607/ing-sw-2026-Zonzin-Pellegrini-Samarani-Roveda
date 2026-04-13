package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.EventModifierEffect;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.deck.CreateEventCard;
import it.polimi.ingsw.mesos.model.enums.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SustenanceEventTest {

    @Test
    void testAllSustenanceEventCardsWithCreateEventCard() {
        SustenanceEvent card1=null;
        SustenanceEvent card2=null;
        SustenanceEvent card3=null;
        List<EventCard> deck = new CreateEventCard("events.json").getAllEventCards();
        for (EventCard c : deck) {
            if (c instanceof SustenanceEvent && c.getEra()== Era.ERA_I) {
                card1 = (SustenanceEvent) c;
            }
            if (c instanceof SustenanceEvent && c.getEra()== Era.ERA_II) {
                card2 = (SustenanceEvent) c;
            }
            if (c instanceof SustenanceEvent&& c.getEra()== Era.ERA_III) {
                card3 = (SustenanceEvent) c;
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

        Hunter hunter = new Hunter(Era.ERA_I,2,true);
        Gatherer gatherer = new Gatherer(Era.ERA_II,4,3);

        SustenanceEvent sustenanceEvent = new SustenanceEvent(Era.ERA_II,2,true,3);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.addFood(6);

        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(gatherer);
        p2.getTribe().addCharacter(gatherer);
        p2.getTribe().addCharacter(gatherer);


        p3.getTribe().addCharacter(hunter);
        p3.getTribe().addCharacter(hunter);
        p3.getTribe().addCharacter(hunter);
        p3.getTribe().addCharacter(hunter);
        p3.setSustenanceDiscount(2);

        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(gatherer);
        p4.addFood(3);
        p4.setSustenanceDiscount(2);

        sustenanceEvent.resolve(game);

        assertEquals(4,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(-6,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(-3,p4.getPrestigePoints());
    }

    @Test
    void testResolveWithBuilding(){
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

        Hunter hunter = new Hunter(Era.ERA_I,2,true);
        Gatherer gatherer = new Gatherer(Era.ERA_II,4,3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        SustenanceEvent sustenanceEvent = new SustenanceEvent(Era.ERA_II,2,true,3);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.addFood(6);

        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(builder);
        p2.getTribe().addCharacter(builder);
        p2.getTribe().addCharacter(gatherer);


        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(hunter);
        p3.getTribe().addCharacter(builder);

        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(gatherer);
        p4.addFood(3);

        BuildingCard building1 = new BuildingCard(Era.ERA_I, 4, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.GATHERER, 1, 0, false, false));

        BuildingCard building2 = new BuildingCard(Era.ERA_I, 5, 3, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.ARTIST, 1, 0, false, false));

        BuildingCard building3 = new BuildingCard(Era.ERA_II, 7, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, 1, 0, false, false));

        building1.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building2.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building3.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);

        building1.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building2.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building3.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);

        building3.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);

        building2.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);

        sustenanceEvent.resolve(game);

        assertEquals(6,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(-3,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(-9,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(-9,p4.getPrestigePoints());
    }

}
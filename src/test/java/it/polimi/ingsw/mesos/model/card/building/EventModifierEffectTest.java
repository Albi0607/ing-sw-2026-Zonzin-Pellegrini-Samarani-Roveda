package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.enums.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventModifierEffectTest {

    @Test
    void testApplyEffectBuilding2() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 4, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.GATHERER, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(gatherer);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(gatherer);
        p3.getTribe().addCharacter(gatherer);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);

        assertEquals(1, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(2, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    @Test
    void testApplyEffectBuilding2WrongCurrentEvent() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 4, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.GATHERER, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(gatherer);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(gatherer);
        p3.getTribe().addCharacter(gatherer);


        building.getEffect().applyEffect(p1, game, TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_PAINTING_EVENT);

        assertEquals(0, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(0, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    //un po ripetitivo, anche quello prima era wrongTrigger
    @Test
    void testApplyEffectBuilding2WrongTrigger() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 4, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.GATHERER, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(gatherer);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(gatherer);
        p3.getTribe().addCharacter(gatherer);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_PURCHASE);

        assertEquals(0, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(0, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }


    @Test
    void testApplyEffectBuilding3() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 5, 3, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.ARTIST, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);

        assertEquals(1, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(2, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    @Test
    void testApplyEffectBuilding3WrongCurrentEvent() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 5, 3, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.ARTIST, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_PAINTING_EVENT);

        assertEquals(0, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(0, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    //un po ripetitivo, anche quello prima era wrongTrigger
    @Test
    void testApplyEffectBuilding3WrongTrigger() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 5, 3, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.ARTIST, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_PURCHASE);

        assertEquals(0, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(0, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    @Test
    void testApplyEffectBuilding4(){
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 5, 2, new EventModifierEffect(null, null, 0, 0, false, true));

        building.getEffect().applyEffect(p1,game,TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_PURCHASE);

        assertTrue(p1.getShamanNotLosePoints());
        assertEquals(0,p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());

        assertFalse(p2.getShamanNotLosePoints());
        assertEquals(0,p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());

        assertTrue(p3.getShamanNotLosePoints());
        assertEquals(0,p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());

        assertFalse(p4.getShamanNotLosePoints());
        assertEquals(0,p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
    }

    @Test
    void testApplyEffectBuilding4WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_I, 5, 2, new EventModifierEffect(null, null, 0, 0, false, true));

        building.getEffect().applyEffect(p1,game,TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_SUSTENANCE_EVENT);

        assertFalse(p1.getShamanNotLosePoints());
        assertEquals(0,p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());

        assertFalse(p2.getShamanNotLosePoints());
        assertEquals(0,p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());

        assertFalse(p3.getShamanNotLosePoints());
        assertEquals(0,p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());

        assertFalse(p4.getShamanNotLosePoints());
        assertEquals(0,p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
    }

    @Test
    void testApplyEffectBuilding7(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 7, 0, new EventModifierEffect(null, null, 0, 0, true, false));

        building.getEffect().applyEffect(p1,game,TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_PURCHASE);

        assertTrue(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());
        assertEquals(0,p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());

        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());
        assertEquals(0,p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());

        assertTrue(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());
        assertEquals(0,p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());

        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
        assertEquals(0,p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());

    }

    @Test
    void testApplyEffectBuilding7WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 7, 0, new EventModifierEffect(null, null, 0, 0, true, false));

        building.getEffect().applyEffect(p1,game,TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_SUSTENANCE_EVENT);

        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());
        assertEquals(0,p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());

        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());
        assertEquals(0,p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());

        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());
        assertEquals(0,p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());

        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
        assertEquals(0,p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());

    }

    @Test
    void testApplyEffectBuilding8(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 6, 4, new EventModifierEffect(null, null, 0, 3, false, false));

        building.getEffect().applyEffect(p1,game,TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_PURCHASE);

        assertEquals(3,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());
        assertEquals(0,p1.getSustenanceDiscount());

        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());
        assertEquals(0,p2.getSustenanceDiscount());

        assertEquals(3,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());
        assertEquals(0,p3.getSustenanceDiscount());

        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
        assertEquals(0,p4.getSustenanceDiscount());
    }

    @Test
    void testApplyEffectBuilding8WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 6, 4, new EventModifierEffect(null, null, 0, 3, false, false));

        building.getEffect().applyEffect(p1,game,TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_SUSTENANCE_EVENT);

        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());
        assertEquals(0,p1.getSustenanceDiscount());

        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());
        assertEquals(0,p2.getSustenanceDiscount());

        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());
        assertEquals(0,p3.getSustenanceDiscount());

        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
        assertEquals(0,p4.getSustenanceDiscount());
    }

    @Test
    void testApplyEffectBuilding9(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 7, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(inventor);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(inventor);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);

        assertEquals(1, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(2, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    @Test
    void testApplyEffectBuilding9WrongCurrentEvent(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 7, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(inventor);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(inventor);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_HUNT_EVENT);

        assertEquals(0, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(0, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }

    //un po ripetitivo, anche quello prima era wrongTrigger
    @Test
    void testApplyEffectBuilding9WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II, 7, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, 1, 0, false, false));

        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(inventor);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(inventor);

        building.getEffect().applyEffect(p1, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p2, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3, game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p4, game, TriggerType.ON_PURCHASE);

        assertEquals(0, p1.getSustenanceDiscount());
        assertEquals(0,p1.getExtraShamanIcons());
        assertFalse(p1.getShamanDoublePoints());
        assertFalse(p1.getShamanNotLosePoints());

        assertEquals(0, p2.getSustenanceDiscount());
        assertEquals(0,p2.getExtraShamanIcons());
        assertFalse(p2.getShamanDoublePoints());
        assertFalse(p2.getShamanNotLosePoints());

        assertEquals(0, p3.getSustenanceDiscount());
        assertEquals(0,p3.getExtraShamanIcons());
        assertFalse(p3.getShamanDoublePoints());
        assertFalse(p3.getShamanNotLosePoints());

        assertEquals(0, p4.getSustenanceDiscount());
        assertEquals(0,p4.getExtraShamanIcons());
        assertFalse(p4.getShamanDoublePoints());
        assertFalse(p4.getShamanNotLosePoints());
    }


    @Test
    void testWithMultipleSustenanceDiscount(){

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
        BuildingCard building1 = new BuildingCard(Era.ERA_II, 7, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, 1, 0, false, false));
        BuildingCard building2 = new BuildingCard(Era.ERA_I, 5, 3, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.ARTIST, 1, 0, false, false));
        BuildingCard building3 = new BuildingCard(Era.ERA_I, 4, 4, new EventModifierEffect(EventType.SUSTENANCE, CharacterType.GATHERER, 1, 0, false, false));


        Hunter hunter = new Hunter(Era.ERA_I, 4, true);
        Gatherer gatherer = new Gatherer(Era.ERA_II, 3, 3);
        Inventor inventor = new Inventor(Era.ERA_III, 3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I, 2, 3);
        Artist artist = new Artist(Era.ERA_II, 4);
        Builder builder = new Builder(Era.ERA_III, 4, 2, 0);


        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(inventor);

        building1.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building1.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building1.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);
        building1.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);
        building2.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building2.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building2.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);
        building2.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);
        building3.getEffect().applyEffect(p1, game, TriggerType.ON_SUSTENANCE_EVENT);
        building3.getEffect().applyEffect(p2, game, TriggerType.ON_SUSTENANCE_EVENT);
        building3.getEffect().applyEffect(p3, game, TriggerType.ON_SUSTENANCE_EVENT);
        building3.getEffect().applyEffect(p4, game, TriggerType.ON_SUSTENANCE_EVENT);

        assertEquals(6, p1.getSustenanceDiscount());

        assertEquals(1, p2.getSustenanceDiscount());

        assertEquals(2, p3.getSustenanceDiscount());

        assertEquals(0, p4.getSustenanceDiscount());

    }

}
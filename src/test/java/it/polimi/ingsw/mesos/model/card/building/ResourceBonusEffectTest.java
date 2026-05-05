package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.common.enums.*;
import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceBonusEffectTest {

    @Test
    void testApplyEffectBuilding1() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I,4,3,new ResourceBonusEffect(null,null, ResourceType.FOOD,5));

        Hunter hunter = new Hunter(Era.ERA_I,4,true);
        Gatherer gatherer = new Gatherer(Era.ERA_II,3,3);
        Inventor inventor = new Inventor(Era.ERA_III,3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I,2,3);
        Artist artist = new Artist(Era.ERA_II,4);
        Builder builder = new Builder(Era.ERA_III,4,2,0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(builder);

        p2.getTribe().addCharacter(hunter);

        p3.getTribe().addCharacter(hunter);
        p3.getTribe().addCharacter(gatherer);
        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(shaman);
        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(builder);
        p3.getTribe().addCharacter(hunter);

        p4.getTribe().addCharacter(builder);
        p4.getTribe().addCharacter(builder);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(inventor);
        p4.getTribe().addCharacter(inventor);
        p4.getTribe().addCharacter(shaman);
        p4.getTribe().addCharacter(shaman);
        p4.getTribe().addCharacter(artist);
        p4.getTribe().addCharacter(artist);


        building.getEffect().applyEffect(p1,game, TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_CHARACTER_ADDED);

        assertEquals(5,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(5,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding1WrongTrigger() {
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
        BuildingCard building = new BuildingCard(Era.ERA_I,4,3,new ResourceBonusEffect(null,null, ResourceType.FOOD,5));

        Hunter hunter = new Hunter(Era.ERA_I,4,true);
        Gatherer gatherer = new Gatherer(Era.ERA_II,3,3);
        Inventor inventor = new Inventor(Era.ERA_III,3, InventionIcon.BOAT);
        Shaman shaman = new Shaman(Era.ERA_I,2,3);
        Artist artist = new Artist(Era.ERA_II,4);
        Builder builder = new Builder(Era.ERA_III,4,2,0);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(builder);

        p2.getTribe().addCharacter(hunter);

        p3.getTribe().addCharacter(hunter);
        p3.getTribe().addCharacter(gatherer);
        p3.getTribe().addCharacter(inventor);
        p3.getTribe().addCharacter(shaman);
        p3.getTribe().addCharacter(artist);
        p3.getTribe().addCharacter(builder);
        p3.getTribe().addCharacter(hunter);

        p4.getTribe().addCharacter(builder);
        p4.getTribe().addCharacter(builder);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(hunter);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(gatherer);
        p4.getTribe().addCharacter(inventor);
        p4.getTribe().addCharacter(inventor);
        p4.getTribe().addCharacter(shaman);
        p4.getTribe().addCharacter(shaman);
        p4.getTribe().addCharacter(artist);
        p4.getTribe().addCharacter(artist);


        building.getEffect().applyEffect(p1,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_HUNT_EVENT);

        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding6(){
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
        BuildingCard building = new BuildingCard(Era.ERA_I,3,4,new ResourceBonusEffect(null, CharacterType.INVENTOR, ResourceType.FOOD,3));

        Inventor inventor1 = new Inventor(Era.ERA_III,3, InventionIcon.BOAT);
        Inventor inventor2 = new Inventor(Era.ERA_II,4, InventionIcon.CLOTH);
        Inventor inventor3 = new Inventor(Era.ERA_III,2, InventionIcon.NECKLACE);
        Inventor inventor4 = new Inventor(Era.ERA_I,2, InventionIcon.BOAT);
        Inventor inventor5 = new Inventor(Era.ERA_III,3, InventionIcon.CLOTH);
        Inventor inventor6 = new Inventor(Era.ERA_I,4, InventionIcon.NECKLACE);

        p1.getTribe().addCharacter(inventor1);
        p1.getTribe().addCharacter(inventor1);
        p1.getTribe().addCharacter(inventor4);

        p2.getTribe().addCharacter(inventor2);
        p2.getTribe().addCharacter(inventor3);
        p2.getTribe().addCharacter(inventor4);

        p3.getTribe().addCharacter(inventor5);

        p4.getTribe().addCharacter(inventor3);
        p4.getTribe().addCharacter(inventor1);
        p4.getTribe().addCharacter(inventor6);


        building.getEffect().applyEffect(p1,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_CHARACTER_ADDED);

        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(3,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding6WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_I,3,4,new ResourceBonusEffect(null,CharacterType.INVENTOR, ResourceType.FOOD,3));

        Inventor inventor1 = new Inventor(Era.ERA_III,3, InventionIcon.BOAT);
        Inventor inventor2 = new Inventor(Era.ERA_II,4, InventionIcon.CLOTH);
        Inventor inventor3 = new Inventor(Era.ERA_III,2, InventionIcon.NECKLACE);
        Inventor inventor4 = new Inventor(Era.ERA_I,2, InventionIcon.BOAT);
        Inventor inventor5 = new Inventor(Era.ERA_III,3, InventionIcon.CLOTH);
        Inventor inventor6 = new Inventor(Era.ERA_I,4, InventionIcon.NECKLACE);

        p1.getTribe().addCharacter(inventor1);
        p1.getTribe().addCharacter(inventor1);
        p1.getTribe().addCharacter(inventor4);

        p2.getTribe().addCharacter(inventor2);
        p2.getTribe().addCharacter(inventor3);
        p2.getTribe().addCharacter(inventor4);

        p3.getTribe().addCharacter(inventor5);

        p4.getTribe().addCharacter(inventor3);
        p4.getTribe().addCharacter(inventor1);
        p4.getTribe().addCharacter(inventor6);


        building.getEffect().applyEffect(p1,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_HUNT_EVENT);

        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding10(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,7,2,new ResourceBonusEffect(EventType.HUNT,CharacterType.HUNTER, null,1));

        Hunter hunter1 = new Hunter(Era.ERA_II,4,false);
        Hunter hunter2 = new Hunter(Era.ERA_II,4,true);

        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter2);

        p2.getTribe().addCharacter(hunter2);

        p3.getTribe().addCharacter(hunter1);

        building.getEffect().applyEffect(p1,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_HUNT_EVENT);


        assertEquals(3,p1.getFood());
        assertEquals(3,p1.getPrestigePoints());

        assertEquals(1,p2.getFood());
        assertEquals(1,p2.getPrestigePoints());

        assertEquals(1,p3.getFood());
        assertEquals(1,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding10WrongCurrentEvent(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,7,2,new ResourceBonusEffect(EventType.HUNT,CharacterType.HUNTER, null,1));

        Hunter hunter1 = new Hunter(Era.ERA_II,4,false);
        Hunter hunter2 = new Hunter(Era.ERA_II,4,true);

        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter2);

        p2.getTribe().addCharacter(hunter2);

        p3.getTribe().addCharacter(hunter1);

        building.getEffect().applyEffect(p1,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_PAINTING_EVENT);


        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    //un po ripetitivo
    @Test
    void testApplyEffectBuilding10WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,7,2,new ResourceBonusEffect(EventType.HUNT,CharacterType.HUNTER, null,1));

        Hunter hunter1 = new Hunter(Era.ERA_II,4,false);
        Hunter hunter2 = new Hunter(Era.ERA_II,4,true);

        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter1);
        p1.getTribe().addCharacter(hunter2);

        p2.getTribe().addCharacter(hunter2);

        p3.getTribe().addCharacter(hunter1);

        building.getEffect().applyEffect(p1,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_CHARACTER_ADDED);


        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding12(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,5,6,new ResourceBonusEffect(EventType.PAINTING,CharacterType.ARTIST, ResourceType.FOOD,1));
        Artist artist = new Artist(Era.ERA_I,2);

        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);

        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_PAINTING_EVENT);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_PAINTING_EVENT);


        assertEquals(3,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(2,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(1,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding12WrongCurrentEvent(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,5,6,new ResourceBonusEffect(EventType.PAINTING,CharacterType.ARTIST, ResourceType.FOOD,1));
        Artist artist = new Artist(Era.ERA_I,2);

        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);

        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_HUNT_EVENT);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_HUNT_EVENT);


        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }

    //un po ripetitivo
    @Test
    void testApplyEffectBuilding12WrongTrigger(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,5,6,new ResourceBonusEffect(EventType.PAINTING,CharacterType.ARTIST, ResourceType.FOOD,1));
        Artist artist = new Artist(Era.ERA_I,2);

        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);

        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p2,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p3,game,TriggerType.ON_CHARACTER_ADDED);
        building.getEffect().applyEffect(p4,game,TriggerType.ON_CHARACTER_ADDED);


        assertEquals(0,p1.getFood());
        assertEquals(0,p1.getPrestigePoints());

        assertEquals(0,p2.getFood());
        assertEquals(0,p2.getPrestigePoints());

        assertEquals(0,p3.getFood());
        assertEquals(0,p3.getPrestigePoints());

        assertEquals(0,p4.getFood());
        assertEquals(0,p4.getPrestigePoints());
    }
}
package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.common.enums.*;
import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EndGameScoringEffectTest {

    @Test
    void testApplyEffectBuilding11() {
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
        BuildingCard building = new BuildingCard(Era.ERA_II,6,4,new EndGameScoringEffect(0,0,true,null));

        Builder builder1 = new Builder(Era.ERA_I,2,1,5);
        Builder builder2 = new Builder(Era.ERA_II,4,2,3);
        Builder builder3 = new Builder(Era.ERA_III,3,2,0);

        p1.getTribe().addCharacter(builder1);
        p1.getTribe().addCharacter(builder2);

        p2.getTribe().addCharacter(builder1);

        p3.getTribe().addCharacter(builder2);

        p4.getTribe().addCharacter(builder3);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(8,p1.getPrestigePoints());

        assertEquals(5,p2.getPrestigePoints());

        assertEquals(3,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding13(){
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
        BuildingCard building = new BuildingCard(Era.ERA_II,5,6,new EndGameScoringEffect(6,0,false,null));

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

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);


        assertEquals(6,p1.getPrestigePoints());

        assertEquals(0,p2.getPrestigePoints());

        assertEquals(6,p3.getPrestigePoints());

        assertEquals(12,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding14(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,8,8,new EndGameScoringEffect(0,3,false, CharacterType.HUNTER));

        Hunter hunter = new Hunter(Era.ERA_I,4,true);

        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(hunter);
        p1.getTribe().addCharacter(hunter);

        p2.getTribe().addCharacter(hunter);
        p2.getTribe().addCharacter(hunter);

        p3.getTribe().addCharacter(hunter);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(9,p1.getPrestigePoints());

        assertEquals(6,p2.getPrestigePoints());

        assertEquals(3,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding15(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,7,6,new EndGameScoringEffect(0,4,false,CharacterType.GATHERER));

        Gatherer gatherer = new Gatherer(Era.ERA_II,4,3);

        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(gatherer);
        p1.getTribe().addCharacter(gatherer);

        p2.getTribe().addCharacter(gatherer);
        p2.getTribe().addCharacter(gatherer);

        p3.getTribe().addCharacter(gatherer);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(12,p1.getPrestigePoints());

        assertEquals(8,p2.getPrestigePoints());

        assertEquals(4,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding16(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,7,4,new EndGameScoringEffect(0,4,false,CharacterType.SHAMAN));

        Shaman shaman = new Shaman(Era.ERA_II,4,3);

        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(shaman);
        p1.getTribe().addCharacter(shaman);

        p2.getTribe().addCharacter(shaman);
        p2.getTribe().addCharacter(shaman);

        p3.getTribe().addCharacter(shaman);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(12,p1.getPrestigePoints());

        assertEquals(8,p2.getPrestigePoints());

        assertEquals(4,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding17(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,6,3,new EndGameScoringEffect(0,4,false,CharacterType.BUILDER));

        Builder builder = new Builder(Era.ERA_II,4,2,3);

        p1.getTribe().addCharacter(builder);
        p1.getTribe().addCharacter(builder);
        p1.getTribe().addCharacter(builder);

        p2.getTribe().addCharacter(builder);
        p2.getTribe().addCharacter(builder);

        p3.getTribe().addCharacter(builder);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(12,p1.getPrestigePoints());

        assertEquals(8,p2.getPrestigePoints());

        assertEquals(4,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding18(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,7,4,new EndGameScoringEffect(0,4,false,CharacterType.ARTIST));

        Artist artist = new Artist(Era.ERA_II,4);

        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);
        p1.getTribe().addCharacter(artist);

        p2.getTribe().addCharacter(artist);
        p2.getTribe().addCharacter(artist);

        p3.getTribe().addCharacter(artist);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(12,p1.getPrestigePoints());

        assertEquals(8,p2.getPrestigePoints());

        assertEquals(4,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding19(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,6,6,new EndGameScoringEffect(0,2,false,CharacterType.INVENTOR));

        Inventor inventor = new Inventor(Era.ERA_II,4,InventionIcon.BOAT);

        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(inventor);
        p1.getTribe().addCharacter(inventor);

        p2.getTribe().addCharacter(inventor);
        p2.getTribe().addCharacter(inventor);

        p3.getTribe().addCharacter(inventor);

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p2,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p4,game, TriggerType.END_GAME);

        assertEquals(6,p1.getPrestigePoints());

        assertEquals(4,p2.getPrestigePoints());

        assertEquals(2,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());
    }

    @Test
    void testApplyEffectBuilding21(){
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
        BuildingCard building = new BuildingCard(Era.ERA_III,10,0,new EndGameScoringEffect(0,25,false,null));

        building.getEffect().applyEffect(p1,game, TriggerType.END_GAME);
        building.getEffect().applyEffect(p3,game, TriggerType.END_GAME);

        assertEquals(25,p1.getPrestigePoints());

        assertEquals(0,p2.getPrestigePoints());

        assertEquals(25,p3.getPrestigePoints());

        assertEquals(0,p4.getPrestigePoints());

    }


}
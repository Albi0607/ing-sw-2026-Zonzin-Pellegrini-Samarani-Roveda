package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HunterTest {

    @Test
    void TestHasIconTrue() {
        Hunter hunter = new Hunter(Era.ERA_III, 3, true);
        assertTrue(hunter.hasIcon());
    }
    @Test
    void TestHasIconFalse() {
        Hunter hunter = new Hunter(Era.ERA_III,3,false);
        assertFalse(hunter.hasIcon());
    }


    @Test
    void TestOnAddedToTribeNoIcon() {
        Player player = new Player("Alberto", Color.BLUE);
        Hunter hunter = new Hunter(Era.ERA_III,3,false);
        player.getTribe().addCharacter(hunter);
        hunter.onAddedToTribe(player);
        assertEquals(0,player.getFood());
    }

    @Test
    void TestOnAddedToTribeIconOne() {
        Player player = new Player("Alberto", Color.BLUE);
        Hunter hunter = new Hunter(Era.ERA_III,3,true);
        player.getTribe().addCharacter(hunter);
        hunter.onAddedToTribe(player);
        assertEquals(1,player.getFood());
    }

    @Test
    void TestOnAddedToTribeIconFive() {
        Player player = new Player("Alberto", Color.BLUE);
        Hunter hunter1 = new Hunter(Era.ERA_I,3,false);
        Hunter hunter2 = new Hunter(Era.ERA_II,3,false);
        Hunter hunter3 = new Hunter(Era.ERA_II,3,false);
        Gatherer gatherer = new Gatherer(Era.ERA_II,2,3);
        Hunter hunter4 = new Hunter(Era.ERA_II,3,false);
        Hunter hunter5 = new Hunter(Era.ERA_III,3,true);
        player.getTribe().addCharacter(hunter1);
        player.getTribe().addCharacter(hunter2);
        player.getTribe().addCharacter(gatherer);
        player.getTribe().addCharacter(hunter3);
        player.getTribe().addCharacter(hunter4);
        player.getTribe().addCharacter(hunter5);
        hunter5.onAddedToTribe(player);
        assertEquals(5,player.getFood());
    }

    @Test
    void testAllHunterCardsWithCreateCharacterCard() {
        List<Hunter> list1 = new ArrayList<>();
        List<Hunter> list2 = new ArrayList<>();
        List<Hunter> list3 = new ArrayList<>();
        List<CharacterCard> deck = new CreateCharacterCard("characters.json").getAllCharacterCards();
        for (CharacterCard c : deck) {
            if (c instanceof Hunter && c.getEra()==Era.ERA_I) {
                list1.add((Hunter) c);
            }
            if (c instanceof Hunter && c.getEra()==Era.ERA_II) {
                list2.add((Hunter) c);
            }
            if (c instanceof Hunter && c.getEra()==Era.ERA_III) {
                list3.add((Hunter) c);
            }
        }

        assertTrue(list1.get(0).hasIcon());
        assertEquals(2,list1.get(0).getPlayerRequired());
        assertTrue(list1.get(1).hasIcon());
        assertEquals(2,list1.get(1).getPlayerRequired());
        assertFalse(list1.get(2).hasIcon());
        assertEquals(2,list1.get(2).getPlayerRequired());
        assertFalse(list1.get(3).hasIcon());
        assertEquals(3,list1.get(3).getPlayerRequired());
        //era 2
        assertFalse(list2.get(0).hasIcon());
        assertEquals(2,list2.get(0).getPlayerRequired());
        assertFalse(list2.get(1).hasIcon());
        assertEquals(2,list2.get(1).getPlayerRequired());
        assertTrue(list2.get(2).hasIcon());
        assertEquals(3,list2.get(2).getPlayerRequired());
        assertTrue(list2.get(3).hasIcon());
        assertEquals(2,list2.get(3).getPlayerRequired());
        assertTrue(list2.get(4).hasIcon());
        assertEquals(4,list2.get(4).getPlayerRequired());
        assertFalse(list2.get(5).hasIcon());
        assertEquals(5,list2.get(5).getPlayerRequired());
        //era 3
        assertTrue(list3.get(0).hasIcon());
        assertEquals(5,list3.get(0).getPlayerRequired());
        assertFalse(list3.get(1).hasIcon());
        assertEquals(2,list3.get(1).getPlayerRequired());
        assertFalse(list3.get(2).hasIcon());
        assertEquals(2,list3.get(2).getPlayerRequired());
        assertTrue(list3.get(3).hasIcon());
        assertEquals(2,list3.get(3).getPlayerRequired());
    }


}
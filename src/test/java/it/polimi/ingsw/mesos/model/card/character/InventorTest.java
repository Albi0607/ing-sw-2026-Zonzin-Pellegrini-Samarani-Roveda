package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.InventionIcon;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventorTest {

    @Test
    void TestGetIcon() {
        Inventor inventor = new Inventor(Era.ERA_II,3, InventionIcon.BOAT);
        assertEquals(InventionIcon.BOAT,inventor.getIcon());
    }


    @Test
    void testAllInventorCardsWithCreateCharacterCard() {
        List<Inventor> list1 = new ArrayList<>();
        List<Inventor> list2 = new ArrayList<>();
        List<Inventor> list3 = new ArrayList<>();
        List<CharacterCard> deck = new CreateCharacterCard("characters.json").getAllCharacterCards();
        for (CharacterCard c : deck) {
            if (c instanceof Inventor && c.getEra()==Era.ERA_I) {
                list1.add((Inventor) c);
            }
            if (c instanceof Inventor && c.getEra()==Era.ERA_II) {
                list2.add((Inventor) c);
            }
            if (c instanceof Inventor && c.getEra()==Era.ERA_III) {
                list3.add((Inventor) c);
            }
        }

        assertEquals(InventionIcon.CLOTH,list1.get(0).getIcon());
        assertEquals(2,list1.get(0).getPlayerRequired());
        assertEquals(InventionIcon.BOAT,list1.get(1).getIcon());
        assertEquals(2,list1.get(1).getPlayerRequired());
        assertEquals(InventionIcon.SPEAR,list1.get(2).getIcon());
        assertEquals(2,list1.get(2).getPlayerRequired());
        assertEquals(InventionIcon.BREAD,list1.get(3).getIcon());
        assertEquals(2,list1.get(3).getPlayerRequired());
        assertEquals(InventionIcon.ROPE,list1.get(4).getIcon());
        assertEquals(4,list1.get(4).getPlayerRequired());
        assertEquals(InventionIcon.STICK,list1.get(5).getIcon());
        assertEquals(4,list1.get(5).getPlayerRequired());
        assertEquals(InventionIcon.BOWL,list1.get(6).getIcon());
        assertEquals(4,list1.get(6).getPlayerRequired());
        //era 2
        assertEquals(InventionIcon.DOLL,list2.get(0).getIcon());
        assertEquals(2,list2.get(0).getPlayerRequired());
        assertEquals(InventionIcon.HOOK,list2.get(1).getIcon());
        assertEquals(4,list2.get(1).getPlayerRequired());
        assertEquals(InventionIcon.ROPE,list2.get(2).getIcon());
        assertEquals(2,list2.get(2).getPlayerRequired());
        assertEquals(InventionIcon.STICK,list2.get(3).getIcon());
        assertEquals(2,list2.get(3).getPlayerRequired());
        assertEquals(InventionIcon.BOWL,list2.get(4).getIcon());
        assertEquals(2,list2.get(4).getPlayerRequired());
        assertEquals(InventionIcon.CLOTH,list2.get(5).getIcon());
        assertEquals(2,list2.get(5).getPlayerRequired());
        //era 3
        assertEquals(InventionIcon.NECKLACE,list3.get(0).getIcon());
        assertEquals(4,list3.get(0).getPlayerRequired());
        assertEquals(InventionIcon.BOAT,list3.get(1).getIcon());
        assertEquals(3,list3.get(1).getPlayerRequired());
        assertEquals(InventionIcon.SPEAR,list3.get(2).getIcon());
        assertEquals(3,list3.get(2).getPlayerRequired());
        assertEquals(InventionIcon.DOLL,list3.get(3).getIcon());
        assertEquals(2,list3.get(3).getPlayerRequired());
        assertEquals(InventionIcon.HOOK,list3.get(4).getIcon());
        assertEquals(2,list3.get(4).getPlayerRequired());
        assertEquals(InventionIcon.NECKLACE,list3.get(5).getIcon());
        assertEquals(2,list3.get(5).getPlayerRequired());
        assertEquals(InventionIcon.BREAD,list3.get(6).getIcon());
        assertEquals(2,list3.get(6).getPlayerRequired());
    }

}
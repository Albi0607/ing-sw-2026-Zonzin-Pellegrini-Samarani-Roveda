package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.common.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShamanTest {

    @Test
    void TestGetNumberOfIcons() {
        Shaman shaman1 = new Shaman(Era.ERA_I,2,3);
        Shaman shaman2 = new Shaman(Era.ERA_I,3,2);
        Shaman shaman3 = new Shaman(Era.ERA_I,5,1);

        assertEquals(3,shaman1.getNumberOfIcons());
        assertEquals(2,shaman2.getNumberOfIcons());
        assertEquals(1,shaman3.getNumberOfIcons());
    }


    @Test
    void testAllShamanCardsWithCreateCharacterCard() {
        List<Shaman> list1 = new ArrayList<>();
        List<Shaman> list2 = new ArrayList<>();
        List<Shaman> list3 = new ArrayList<>();
        List<CharacterCard> deck = new CreateCharacterCard("cards/characters.json").getAllCharacterCards();
        for (CharacterCard c : deck) {
            if (c instanceof Shaman && c.getEra()==Era.ERA_I) {
                list1.add((Shaman) c);
            }
            if (c instanceof Shaman && c.getEra()==Era.ERA_II) {
                list2.add((Shaman) c);
            }
            if (c instanceof Shaman && c.getEra()==Era.ERA_III) {
                list3.add((Shaman) c);
            }
        }

        assertEquals(2,list1.get(0).getNumberOfIcons());
        assertEquals(5,list1.get(0).getPlayerRequired());
        assertEquals(2,list1.get(1).getNumberOfIcons());
        assertEquals(2,list1.get(1).getPlayerRequired());
        assertEquals(1,list1.get(2).getNumberOfIcons());
        assertEquals(2,list1.get(2).getPlayerRequired());
        assertEquals(1,list1.get(3).getNumberOfIcons());
        assertEquals(4,list1.get(3).getPlayerRequired());
        //era 2
        assertEquals(2,list2.get(0).getNumberOfIcons());
        assertEquals(2,list2.get(0).getPlayerRequired());
        assertEquals(2,list2.get(1).getNumberOfIcons());
        assertEquals(2,list2.get(1).getPlayerRequired());
        assertEquals(1,list2.get(2).getNumberOfIcons());
        assertEquals(5,list2.get(2).getPlayerRequired());
        assertEquals(2,list2.get(3).getNumberOfIcons());
        assertEquals(5,list2.get(3).getPlayerRequired());
        //era 3
        assertEquals(2,list3.get(0).getNumberOfIcons());
        assertEquals(3,list3.get(0).getPlayerRequired());
        assertEquals(3,list3.get(1).getNumberOfIcons());
        assertEquals(2,list3.get(1).getPlayerRequired());
        assertEquals(2,list3.get(2).getNumberOfIcons());
        assertEquals(2,list3.get(2).getPlayerRequired());
        assertEquals(3,list3.get(3).getNumberOfIcons());
        assertEquals(2,list3.get(3).getPlayerRequired());
        assertEquals(2,list3.get(4).getNumberOfIcons());
        assertEquals(4,list3.get(4).getPlayerRequired());
    }
}
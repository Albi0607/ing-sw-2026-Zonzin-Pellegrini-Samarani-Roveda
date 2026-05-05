package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.common.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GathererTest {
    @Test
    void testGetFoodDiscount(){
        Gatherer gatherer = new Gatherer(Era.ERA_III, 4,3);
        assertEquals(3,gatherer.getFoodDiscount());
    }

    @Test
    void testAllGathererCardsWithCreateCharacterCard() {
        List<Gatherer> list1 = new ArrayList<>();
        List<Gatherer> list2 = new ArrayList<>();
        List<Gatherer> list3 = new ArrayList<>();
        List<CharacterCard> deck = new CreateCharacterCard("characters.json").getAllCharacterCards();
        for (CharacterCard c : deck) {
            if (c instanceof Gatherer && c.getEra()==Era.ERA_I) {
                list1.add((Gatherer) c);
            }
            if (c instanceof Gatherer && c.getEra()==Era.ERA_II) {
                list2.add((Gatherer) c);
            }
            if (c instanceof Gatherer && c.getEra()==Era.ERA_III) {
                list3.add((Gatherer) c);
            }
        }

        assertEquals(3,list1.get(0).getFoodDiscount());
        assertEquals(2,list1.get(0).getPlayerRequired());
        assertEquals(3,list1.get(1).getFoodDiscount());
        assertEquals(2,list1.get(1).getPlayerRequired());
        assertEquals(3,list1.get(2).getFoodDiscount());
        assertEquals(3,list1.get(2).getPlayerRequired());
        assertEquals(3,list1.get(3).getFoodDiscount());
        assertEquals(5,list1.get(3).getPlayerRequired());
        //era 2
        assertEquals(3,list2.get(0).getFoodDiscount());
        assertEquals(2,list2.get(0).getPlayerRequired());
        assertEquals(3,list2.get(1).getFoodDiscount());
        assertEquals(3,list2.get(1).getPlayerRequired());
        assertEquals(3,list2.get(2).getFoodDiscount());
        assertEquals(4,list2.get(2).getPlayerRequired());
        assertEquals(3,list2.get(3).getFoodDiscount());
        assertEquals(5,list2.get(3).getPlayerRequired());
        //era 3
        assertEquals(3,list3.get(0).getFoodDiscount());
        assertEquals(5,list3.get(0).getPlayerRequired());
        assertEquals(3,list3.get(1).getFoodDiscount());
        assertEquals(4,list3.get(1).getPlayerRequired());
        assertEquals(3,list3.get(2).getFoodDiscount());
        assertEquals(2,list3.get(2).getPlayerRequired());
    }

}
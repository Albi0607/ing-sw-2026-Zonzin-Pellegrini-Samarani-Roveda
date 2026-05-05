package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.common.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuilderTest {

    @Test
    void testGetPrestigePoints() {
        Builder builder = new Builder(Era.ERA_I,2,1,3);
        assertEquals(3,builder.getPrestigePoints());
    }

    @Test
    void testGetDiscountValue() {
        Builder builder = new Builder(Era.ERA_II,4,2,1);
        assertEquals(2,builder.getDiscountValue());
    }

    @Test
    void testAllBuilderCardsWithCreateCharacterCard() {
        List<Builder> list1 = new ArrayList<>();
        List<Builder> list2 = new ArrayList<>();
        List<Builder> list3 = new ArrayList<>();
        List<CharacterCard> deck = new CreateCharacterCard("characters.json").getAllCharacterCards();
        for (CharacterCard c : deck) {
            if (c instanceof Builder && c.getEra()==Era.ERA_I) {
                list1.add((Builder) c);
            }
            if (c instanceof Builder && c.getEra()==Era.ERA_II) {
                list2.add((Builder) c);
            }
            if (c instanceof Builder && c.getEra()==Era.ERA_III) {
                list3.add((Builder) c);
            }
        }

        assertEquals(1,list1.get(0).getDiscountValue());
        assertEquals(3,list1.get(0).getPrestigePoints());
        assertEquals(2,list1.get(1).getDiscountValue());
        assertEquals(0,list1.get(1).getPrestigePoints());
        assertEquals(2,list1.get(2).getDiscountValue());
        assertEquals(1,list1.get(2).getPrestigePoints());
        assertEquals(1,list1.get(3).getDiscountValue());
        assertEquals(2,list1.get(3).getPrestigePoints());
        //era 2
        assertEquals(1,list2.get(0).getDiscountValue());
        assertEquals(4,list2.get(0).getPrestigePoints());
        assertEquals(2,list2.get(1).getDiscountValue());
        assertEquals(1,list2.get(1).getPrestigePoints());
        assertEquals(1,list2.get(2).getDiscountValue());
        assertEquals(2,list2.get(2).getPrestigePoints());
        assertEquals(2,list2.get(3).getDiscountValue());
        assertEquals(3,list2.get(3).getPrestigePoints());
        //era 3
        assertEquals(1,list3.get(0).getDiscountValue());
        assertEquals(5,list3.get(0).getPrestigePoints());
        assertEquals(2,list3.get(1).getDiscountValue());
        assertEquals(3,list3.get(1).getPrestigePoints());
        assertEquals(1,list3.get(2).getDiscountValue());
        assertEquals(4,list3.get(2).getPrestigePoints());
        assertEquals(2,list3.get(3).getDiscountValue());
        assertEquals(2,list3.get(3).getPrestigePoints());
    }


}
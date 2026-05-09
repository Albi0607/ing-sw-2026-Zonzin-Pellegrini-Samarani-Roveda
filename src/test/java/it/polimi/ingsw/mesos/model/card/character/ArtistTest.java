package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.common.enums.Era;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void testAllArtistCardsWithCreateCharacterCard() {
        List<Artist> list1 = new ArrayList<>();
        List<Artist> list2 = new ArrayList<>();
        List<Artist> list3 = new ArrayList<>();
        List<CharacterCard> deck = new CreateCharacterCard("cards/characters.json").getAllCharacterCards();
        for (CharacterCard c : deck) {
            if (c instanceof Artist && c.getEra()== Era.ERA_I) {
                list1.add((Artist) c);
            }
            if (c instanceof Artist && c.getEra()== Era.ERA_II) {
                list2.add((Artist) c);
            }
            if (c instanceof Artist && c.getEra()== Era.ERA_III) {
                list3.add((Artist) c);
            }
        }

        assertEquals(2,list1.get(0).getPlayerRequired());
        assertEquals(2,list1.get(1).getPlayerRequired());
        assertEquals(2,list1.get(2).getPlayerRequired());
        assertEquals(3,list1.get(3).getPlayerRequired());
        assertEquals(4,list1.get(4).getPlayerRequired());
        //era 2
        assertEquals(3,list2.get(0).getPlayerRequired());
        assertEquals(2,list2.get(1).getPlayerRequired());
        assertEquals(2,list2.get(2).getPlayerRequired());
        assertEquals(2,list2.get(3).getPlayerRequired());
        //era 3
        assertEquals(5,list3.get(0).getPlayerRequired());
        assertEquals(2,list3.get(1).getPlayerRequired());
        assertEquals(2,list3.get(2).getPlayerRequired());
        assertEquals(2,list3.get(3).getPlayerRequired());
    }


}
package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateCharacterCardTest {
    @Test
    void testCorrectNumberOfCard(){
        List<CharacterCard> list = new CreateCharacterCard("characters.json").getAllCharacterCards();
        assertEquals(84,list.size());
    }

}
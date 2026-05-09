package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateCharacterCardTest {
    @Test
    void testCorrectNumberOfCardFor5Players() {
        List<CharacterCard> list = new CreateCharacterCard("cards/characters.json").getAllCharacterCards();
        assertEquals(84, list.size());
    }

    @Test
    void testWrongInputFromJson(){
        assertThrows(RuntimeException.class,()->new CreateCharacterCard("cards/wrongCharactersForTest.json"));
    }

    @Test
    void testWrongFile(){
        assertThrows(RuntimeException.class,()->new CreateCharacterCard("prova.json"));
    }

}
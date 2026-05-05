package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.Era;
import it.polimi.ingsw.mesos.common.enums.InventionIcon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardTest {

    @Test
    void getCharacterType() {
        Shaman shaman = new Shaman(Era.ERA_I,3,3);
        Gatherer gatherer = new Gatherer(Era.ERA_III,4,3);
        Inventor inventor = new Inventor(Era.ERA_I,5, InventionIcon.BOAT);
        Artist artist = new Artist(Era.ERA_III,2);
        Builder builder = new Builder(Era.ERA_III,4,1,4);
        Hunter hunter = new Hunter(Era.ERA_I,2,true);

        assertEquals(CharacterType.SHAMAN,shaman.getCharacterType());
        assertEquals(CharacterType.GATHERER,gatherer.getCharacterType());
        assertEquals(CharacterType.INVENTOR,inventor.getCharacterType());
        assertEquals(CharacterType.ARTIST,artist.getCharacterType());
        assertEquals(CharacterType.BUILDER,builder.getCharacterType());
        assertEquals(CharacterType.HUNTER,hunter.getCharacterType());
    }
}
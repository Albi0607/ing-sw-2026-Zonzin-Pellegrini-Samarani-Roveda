package it.polimi.ingsw.mesos.model.BoardTest;

import it.polimi.ingsw.mesos.model.board.*;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.EndGameScoringEffect;
import it.polimi.ingsw.mesos.model.card.character.Artist;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BoardTest {
    @Test
    void testRefillRowsAddsCards() {
        Board board = new Board(2);

        Artist c1 = new Artist(Era.ERA_I, 2);
        Artist c2 = new Artist(Era.ERA_I, 2);
        Artist c3 = new Artist(Era.ERA_I, 4);

        board.getTribeDeck().put(c1);
        board.getTribeDeck().put(c2);
        board.getTribeDeck().put(c3);

        // La riga superiore inizialmente è vuota
        assertEquals(0, board.getUpperRow().size());

        board.refillRows(3);

        assertEquals(3, board.getUpperRow().size());
    }

    @Test
    void testRefillRowsDoesNotOverfill() {
        Board board = new Board(2);

        Artist c1 = new Artist(Era.ERA_I, 2);
        Artist c2 = new Artist(Era.ERA_I, 3);
        Artist c3 = new Artist(Era.ERA_I, 1);

        board.getTribeDeck().put(c1);
        board.getTribeDeck().put(c2);
        board.getTribeDeck().put(c3);

        // Inseriamo già una carta nella riga
        board.getUpperRow().add(new Artist(Era.ERA_I, 1));

        board.refillRows(3);

        // Ora la riga deve contenere 3 carte in totale
        assertEquals(3, board.getUpperRow().size());
    }

    @Test
    void testRefillRowsWithEmptyDeck() {
        Board board = new Board(2);

        // Mazzo vuoto
        board.refillRows(3);

        // La riga rimane vuota
        assertEquals(0, board.getUpperRow().size());
    }

    @Test
    void testShiftUpperToLower() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        board.getUpperRow().add(c1);
        board.getUpperRow().add(c2);

        board.shiftUpperToLower();

        assertTrue(board.getLowerRow().contains(c1)); // the method have to move c1
        assertFalse(board.getUpperRow().contains(c1)); // the method don't have to move c2, because it is a building
        assertTrue(board.getUpperRow().contains(c2)); // c2 remains in the upper row
        assertFalse(board.getLowerRow().contains(c2));
    }

    @Test
    void testClearLowerRow() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        board.getLowerRow().add(c1);
        board.getLowerRow().add(c2);

        board.clearLowerRow();

        assertFalse(board.getLowerRow().contains(c1)); // remove
        assertTrue(board.getLowerRow().contains(c2));  // remain
    }

    @Test
    void clearBuildingsFromLower() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        board.getLowerRow().add(c1);
        board.getLowerRow().add(c2);

        board.clearBuildingsFromLower();

        assertTrue(board.getLowerRow().contains(c1)); // remove
        assertFalse(board.getLowerRow().contains(c2));  // remain
    }

    @Test
    void shiftBuildingsToLower() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        board.getUpperRow().add(c1);
        board.getUpperRow().add(c2);

        board.shiftBuildingsToLower();

        assertFalse(board.getLowerRow().contains(c1)); // the method do not have to move c1
        assertTrue(board.getUpperRow().contains(c1)); // the method have to move c2, because it is a building
        assertFalse(board.getUpperRow().contains(c2)); // c2 have to be in the lower row
        assertTrue(board.getLowerRow().contains(c2));
    }

    @Test
    void takeCardFromLower() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        board.getLowerRow().add(c1);
        board.getLowerRow().add(c2);
        board.getLowerRow().add(c3);

        Card taken = board.takeCardFromLower(2);

        assertEquals(c3, taken);
        assertFalse(board.getLowerRow().contains(c3));
        assertNotEquals(c1, taken);
        assertNotEquals(c2, taken);
        assertTrue(board.getLowerRow().contains(c1));
        assertTrue(board.getLowerRow().contains(c2));
    }

    @Test
    void testTakeCardFromUpper() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        board.getUpperRow().add(c1);
        board.getUpperRow().add(c2);
        board.getUpperRow().add(c3);

        Card taken = board.takeCardFromUpper(2);

        assertEquals(c3, taken);
        assertFalse(board.getUpperRow().contains(c3));
        assertNotEquals(c1, taken);
        assertNotEquals(c2, taken);
        assertTrue(board.getUpperRow().contains(c1));
        assertTrue(board.getUpperRow().contains(c2));
    }

    @Test
    void testTakeCardFromLowerInvalidIndex() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        board.getLowerRow().add(c1);
        board.getLowerRow().add(c2);
        board.getLowerRow().add(c3);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.takeCardFromLower(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.takeCardFromLower(7);
        });
    }

    @Test
    void testTakeCardFromUpperInvalidIndex() {
        Board board = new Board(2);

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        board.getUpperRow().add(c1);
        board.getUpperRow().add(c2);
        board.getUpperRow().add(c3);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.takeCardFromUpper(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.takeCardFromUpper(7);
        });
    }
    // getAvailableTiles

    // initializeOfferTiles
    @Test
    void testInitializeOfferTiles() {
        Board board = new Board(4);
        CreateOfferTile factory = new CreateOfferTile();
        List<OfferTile> tiles = factory.initializeOfferTiles(4);

        board.setTiles(tiles);

        assertNotNull(tiles);
        assertFalse(tiles.isEmpty());

        // all the Offer Tile have to be consistent with the number of players
        for (OfferTile t : tiles) {
            assertTrue(t.getMinPlayers() <= 4);
        }
    }

    @Test
    void testInitializeOfferTiles_invalidPlayersTooLow() {
        CreateOfferTile factory = new CreateOfferTile();

        assertThrows(IllegalArgumentException.class, () -> {
            factory.initializeOfferTiles(1);
        });
    }
    @Test
    void testInitializeOfferTiles_invalidPlayersTooHigh() {
        CreateOfferTile factory = new CreateOfferTile();

        assertThrows(IllegalArgumentException.class, () -> {
            factory.initializeOfferTiles(6);
        });
    }

    //initializeTurnOrderTrack
    @Test
    void testInitializeTurnOrderTrack_allValidPlayers() {
        Board board = new Board(2);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();

        // expected slot based on the number of players
        int[][] expectedSlots = {
                {1, -1},           // 2 players
                {2, 0, -1},        // 3 players
                {2, 1, 0, -1},     // 4 players
                {3, 1, 0, 0, -1}   // 5 players
        };

        for (int i = 2; i <= 5; i++) {
            board.setTurnOrderTrack(factory.initializeTurnOrderTrack(i));
            assertArrayEquals(expectedSlots[i - 2], board.getTurnOrderTrack().getSlots());
        }
    }

    @Test
    void testInitializeTurnOrderTrack_invalidTooLow() {
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();

        assertThrows(IllegalArgumentException.class, () -> {
            factory.initializeTurnOrderTrack(1);
        });
    }

    @Test
    void testInitializeTurnOrderTrack_invalidTooHigh() {
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();

        assertThrows(IllegalArgumentException.class, () -> {
            factory.initializeTurnOrderTrack(6);
        });
    }

}






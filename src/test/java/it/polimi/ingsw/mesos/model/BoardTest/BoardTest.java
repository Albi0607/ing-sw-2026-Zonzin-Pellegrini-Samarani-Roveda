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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BoardTest {
    @Test
    void testRefillRows() {
        Board board = new Board(2);

        Deck<Artist> deck = new Deck<>();
        //deck.add(new Artist(Era.ERA_I,2));
       // deck.add(new TribeCard());

        //board.refillRows(deck, 2);

        assertEquals(2, board.getUpperRow().size());
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

        board.setOfferTiles(tiles);

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






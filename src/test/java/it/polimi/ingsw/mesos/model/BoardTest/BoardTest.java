
package it.polimi.ingsw.mesos.model.BoardTest;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.*;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.EndGameScoringEffect;
import it.polimi.ingsw.mesos.model.card.character.Artist;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BoardTest {
    private Game game;
    private List<Player> players;


    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player("Alice", Color.RED));
        players.add(new Player("Bob", Color.BLUE));
        players.add(new Player("francesco", Color.YELLOW));
        players.add(new Player("Matteo", Color.WHITE));
        game = new Game(players);
    }

    @Test
    void testRefillRowsAddsCards() {

        Artist c1 = new Artist(Era.ERA_I, 2);
        Artist c2 = new Artist(Era.ERA_I, 2);
        Artist c3 = new Artist(Era.ERA_I, 4);

        game.getBoard().getTribeDeck().put(c1);
        game.getBoard().getTribeDeck().put(c2);
        game.getBoard().getTribeDeck().put(c3);

        // La riga superiore inizialmente è vuota
        assertEquals(0, game.getBoard().getUpperRow().size());

        game.getBoard().refillRows(3,game);

        assertEquals(3, game.getBoard().getUpperRow().size());
    }
    
    @Test
    void testShiftUpperToLower() {

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        game.getBoard().getUpperRow().add(c1);
        game.getBoard().getUpperRow().add(c2);

        game.getBoard().shiftUpperToLower();

        assertTrue(game.getBoard().getLowerRow().contains(c1)); // the method have to move c1
        assertFalse(game.getBoard().getUpperRow().contains(c1)); // the method don't have to move c2, because it is a building
        assertTrue(game.getBoard().getUpperRow().contains(c2)); // c2 remains in the upper row
        assertFalse(game.getBoard().getLowerRow().contains(c2));
    }

    @Test
    void testClearLowerRow() {

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        game.getBoard().getLowerRow().add(c1);
        game.getBoard().getLowerRow().add(c2);

        game.getBoard().clearLowerRow();

        assertFalse(game.getBoard().getLowerRow().contains(c1)); // remove
    }

    @Test
    void clearBuildingsFromLower() {

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        game.getBoard().getLowerRow().add(c1);
        game.getBoard().getLowerRow().add(c2);

        game.getBoard().clearBuildingsFromLower();

        assertTrue(game.getBoard().getLowerRow().contains(c1)); // remove
        assertFalse(game.getBoard().getLowerRow().contains(c2));  // remain
    }

    @Test
    void shiftBuildingsToLower() {

        Card c1 = new Artist(Era.ERA_I,2);     // not a building
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building

        game.getBoard().getUpperRow().add(c1);
        game.getBoard().getUpperRow().add(c2);

        game.getBoard().shiftBuildingsToLower();

        assertFalse(game.getBoard().getLowerRow().contains(c1)); // the method do not have to move c1
        assertTrue(game.getBoard().getUpperRow().contains(c1)); // the method have to move c2, because it is a building
        assertFalse(game.getBoard().getUpperRow().contains(c2)); // c2 have to be in the lower row
        assertTrue(game.getBoard().getLowerRow().contains(c2));
    }

    @Test
    void takeCardFromLower() {

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        game.getBoard().getLowerRow().add(c1);
        game.getBoard().getLowerRow().add(c2);
        game.getBoard().getLowerRow().add(c3);

        Card taken = game.getBoard().takeCardFromLower(2);

        assertEquals(c3, taken);
        assertFalse(game.getBoard().getLowerRow().contains(c3));
        assertNotEquals(c1, taken);
        assertNotEquals(c2, taken);
        assertTrue(game.getBoard().getLowerRow().contains(c1));
        assertTrue(game.getBoard().getLowerRow().contains(c2));
    }

    @Test
    void testTakeCardFromUpper() {

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        game.getBoard().getUpperRow().add(c1);
        game.getBoard().getUpperRow().add(c2);
        game.getBoard().getUpperRow().add(c3);

        Card taken = game.getBoard().takeCardFromUpper(2);

        assertEquals(c3, taken);
        assertFalse(game.getBoard().getUpperRow().contains(c3));
        assertNotEquals(c1, taken);
        assertNotEquals(c2, taken);
        assertTrue(game.getBoard().getUpperRow().contains(c1));
        assertTrue(game.getBoard().getUpperRow().contains(c2));
    }

    @Test
    void testTakeCardFromLowerInvalidIndex() {

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        game.getBoard().getLowerRow().add(c1);
        game.getBoard().getLowerRow().add(c2);
        game.getBoard().getLowerRow().add(c3);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            game.getBoard().takeCardFromLower(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            game.getBoard().takeCardFromLower(7);
        });
    }

    @Test
    void testTakeCardFromUpperInvalidIndex() {

        Card c1 = new Artist(Era.ERA_I,2);
        Card c2 = new BuildingCard(Era.ERA_I, 1,2,new EndGameScoringEffect(2,2,false, CharacterType.ARTIST));  // building
        Card c3 = new Artist(Era.ERA_I,4);

        game.getBoard().getUpperRow().add(c1);
        game.getBoard().getUpperRow().add(c2);
        game.getBoard().getUpperRow().add(c3);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            game.getBoard().takeCardFromUpper(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            game.getBoard().takeCardFromUpper(7);
        });
    }
    // getAvailableTiles

    // initializeOfferTiles
    @Test
    void testInitializeOfferTiles() {
        CreateOfferTile factory = new CreateOfferTile();
        List<OfferTile> tiles = factory.initializeOfferTiles(4);

        game.getBoard().setTiles(tiles);

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
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();

        // expected slot based on the number of players
        int[][] expectedSlots = {
                {1, -1},           // 2 players
                {2, 0, -1},        // 3 players
                {2, 1, 0, -1},     // 4 players
                {3, 1, 0, 0, -1}   // 5 players
        };

        for (int i = 2; i <= 5; i++) {
            game.getBoard().setTurnOrderTrack(factory.initializeTurnOrderTrack(i));
            assertArrayEquals(expectedSlots[i - 2], game.getBoard().getTurnOrderTrack().getSlots());
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






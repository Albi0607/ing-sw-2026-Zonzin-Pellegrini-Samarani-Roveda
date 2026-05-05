package it.polimi.ingsw.mesos.model.BoardTest;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.common.enums.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTileTest {
    @Test
    void testIsAvailable() {
        OfferTile tile = new OfferTile('B', 2);

        // available
        assertTrue(tile.isAvailable());

        Player p = new Player("Bob", Color.BLUE);
        tile.setHost(p);
        // not available
        assertFalse(tile.isAvailable());
    }

    @Test
    void testPlaceTotemInvalidInput() {
        OfferTile tile = new OfferTile('B', 2);
        assertThrows(IllegalArgumentException.class, () -> {
           tile.placeTotem(null);
        });
    }

    @Test
    void testPlaceTotemIllegalState() {
        OfferTile tile = new OfferTile('B', 2);
        Player p = new Player("Bob", Color.BLUE);
        tile.setHost(p); // not available
        assertThrows(IllegalStateException.class, () -> {
            tile.placeTotem(p);
        });
    }
}

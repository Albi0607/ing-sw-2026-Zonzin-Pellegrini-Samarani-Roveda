package it.polimi.ingsw.mesos.model.BoardTest;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static it.polimi.ingsw.mesos.model.enums.Color.blue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OfferTileTest {
    @Test
    void testIsAvailable() {
        OfferTile tile = new OfferTile('B', 2);

        // inizialmente disponibile
        assertTrue(tile.isAvailable());

        Player p = new Player("Bob","blu");
        tile.setHost(p);

        assertFalse(tile.isAvailable());
    }

    //execute
}

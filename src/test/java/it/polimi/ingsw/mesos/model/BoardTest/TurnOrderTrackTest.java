package it.polimi.ingsw.mesos.model.BoardTest;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnOrderTrackTest {

    @Test
    void testSetPlayerAt() {
        Board board = new Board(3);
        board.initializeTurnOrderTrack(3);
        Player p = new Player("Alice", "red");

        board.getTurnOrderTrack().setPlayerAt(0, p);
        assertEquals(p, board.getTurnOrderTrack().getPlayerAt(0));
    }

    @Test
    void testSetPlayerAtAlreadyTaken() {
        Board board = new Board(3);
        board.initializeTurnOrderTrack(3);
        Player p1 = new Player("Alice", "red");
        Player p2 = new Player("Anna", "blue");

        board.getTurnOrderTrack().setPlayerAt(0, p1);
        assertThrows(IllegalStateException.class, () -> {
            board.getTurnOrderTrack().setPlayerAt(0, p2);
        });
    }

    @Test
    void testSetPlayerAtInvalidIndex() {
        Board board = new Board(3);
        board.initializeTurnOrderTrack(3);
        Player p = new Player("Anna", "blue");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getTurnOrderTrack().setPlayerAt(10, p);
        });
    }

    @Test
    void testGetFirstFreeSlot() {
        Board board = new Board(2);
        board.initializeTurnOrderTrack(2);

        assertEquals(0, board.getTurnOrderTrack().getFirstFreeSlot());

        Player p = new Player("Alice", "red");
        board.getTurnOrderTrack().setPlayerAt(1, p);

        assertEquals(0, board.getTurnOrderTrack().getFirstFreeSlot());

        Player p2 = new Player("Bob", "red");
        board.getTurnOrderTrack().setPlayerAt(0, p2);

        assertEquals(-1, board.getTurnOrderTrack().getFirstFreeSlot()); // all slot occupied
    }


}

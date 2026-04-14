package it.polimi.ingsw.mesos.model.BoardTest;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.CreateTurnOrderTrack;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.enums.Color;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnOrderTrackTest {

    @Test
    void testSetPlayerAt() {
        Board board = new Board(3);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(3);

        board.setTurnOrderTrack(track);
        Player p = new Player("Alice", Color.RED);

        board.getTurnOrderTrack().setPlayerAt(0, p);
        assertEquals(p, board.getTurnOrderTrack().getPlayerAt(0));
    }

    @Test
    void testSetPlayerAtAlreadyTaken() {
        Board board = new Board(3);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(3);

        board.setTurnOrderTrack(track);
        Player p1 = new Player("Alice", Color.RED);
        Player p2 = new Player("Anna", Color.BLUE);

        board.getTurnOrderTrack().setPlayerAt(0, p1);
        assertThrows(IllegalStateException.class, () -> {
            board.getTurnOrderTrack().setPlayerAt(0, p2);
        });
    }

    @Test
    void testSetPlayerAtInvalidIndex() {
        Board board = new Board(3);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(3);

        board.setTurnOrderTrack(track);
        Player p = new Player("Anna", Color.BLUE);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getTurnOrderTrack().setPlayerAt(10, p);
        });
    }

    @Test
    void testSetPlayerAtPositiveEffect() {
        Board board = new Board(2);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(2);

        board.setTurnOrderTrack(track); // slots = {1, -1}
        track.setEffectsActive(true);

        Player p = new Player("Anna", Color.BLUE);

        board.getTurnOrderTrack().setPlayerAt(0, p);

        assertEquals(1, p.getFood()); // slot 0 = +1
    }

    @Test
    void testSetPlayerAtNegativeEffectPaySuccess() {
        Board board = new Board(2);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(2);

        board.setTurnOrderTrack(track); // {1, -1}
        track.setEffectsActive(true);

        Player p = new Player("Anna", Color.BLUE);
        p.addFood(2);

        board.getTurnOrderTrack().setPlayerAt(1, p);

        assertEquals(1, p.getFood()); // 2 - 1
        assertEquals(0, p.getPrestigePoints());
    }

    @Test
    void testSetPlayerAtNegativeEffectNotEnoughFood() {
        Board board = new Board(2);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(2);

        board.setTurnOrderTrack(track); // {1, -1}
        track.setEffectsActive(true);

        Player p = new Player("Anna", Color.BLUE);
        p.addFood(0);
        p.updatePrestige(5);

        board.getTurnOrderTrack().setPlayerAt(1, p);

        assertEquals(0, p.getFood()); // the player can't pay --> food remains the same
        assertEquals(3, p.getPrestigePoints()); // the player pay in prestife points
    }

    @Test
    void testSetPlayerAtWithFoodOnTotemSlotTrue() {
        Board board = new Board(2);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(2);

        board.setTurnOrderTrack(track); // {1, -1}
        track.setEffectsActive(true);

        Player p = new Player("Anna", Color.BLUE);
        p.addFood(3);
        p.setFoodOnTotemSlot();

        board.getTurnOrderTrack().setPlayerAt(0, p);

        assertEquals(5, p.getFood());
    }

    @Test
    void testGetFirstFreeSlot() {
        Board board = new Board(2);
        CreateTurnOrderTrack factory = new CreateTurnOrderTrack();
        TurnOrderTrack track = factory.initializeTurnOrderTrack(2);

        board.setTurnOrderTrack(track);

        assertEquals(0, board.getTurnOrderTrack().getFirstFreeSlot());

        Player p = new Player("Alice", Color.RED);
        board.getTurnOrderTrack().setPlayerAt(1, p);

        assertEquals(0, board.getTurnOrderTrack().getFirstFreeSlot());

        Player p2 = new Player("Bob", Color.RED);
        board.getTurnOrderTrack().setPlayerAt(0, p2);

        assertThrows(IllegalStateException.class, () -> {
            board.getTurnOrderTrack().getFirstFreeSlot();  // all slot occupied
        });
    }


}

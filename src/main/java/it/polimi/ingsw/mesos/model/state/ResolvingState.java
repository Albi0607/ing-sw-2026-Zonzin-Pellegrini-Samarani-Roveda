package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.enums.GameState;

/**
 * Represents the phase where actions on the board are resolved.
 * <p>
 * In this state, the game evaluates the offer track from top to bottom,
 * executing the actions associated with the tiles where totems were placed.
 * Players gather resources, buy cards, and apply effects.
 * After all actions are resolved, it transitions to the {@link EventState}.
 * </p>
 */

public class ResolvingState implements GameStateLogic {

    /**
     * Determines resolution order (left to right on OfferTrack) and
     * waits for each player to complete their picks.
     * When all players are done, transitions to EventState.
     */


    /**
     * Executes the logic for resolving board actions.
     *
     * @param g The main game context. Must not be null.
     */
    @Override
    public void execute(Game g) {

        System.out.println("--- Entering RESOLVING PHASE ---");

        Board board = g.getBoard();
        TurnOrderTrack track = board.getTurnOrderTrack();

        for (OfferTile tile : board.getTiles()) {

            Player host = tile.getHost();

            if (host != null) {

                System.out.println("Resolving tile " + tile.getId() +
                        " for player " + host.getNickname());

                // 1. esegue l'effetto della tile
                tile.execute(host, g);

                // 2. assegna la posizione nella turn order track
                int slot = track.getFirstFreeSlot();

                if (slot != -1) {
                    track.setPlayerAt(slot, host);
                }

                // 3. libera la tile per il prossimo round
                tile.reset();
            }
        }

        System.out.println("All tiles resolved. Turn order updated.");

        g.changeState(new EventState());
    }


    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase di risoluzione!!!");
    }


    @Override
    public GameState getStateId() { return GameState.RESOLVING_ACTIONS; } // probabile da cambiare
}

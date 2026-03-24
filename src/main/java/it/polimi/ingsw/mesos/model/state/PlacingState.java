package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.enums.GameState;

/**
 * Represents the phase where players place their totems on the offer track.
 * <p>
 * During this state, the game waits for players to take turns placing
 * their totems on available {@link OfferTile}s. The state remains active
 * until all totems for the current round have been placed.
 * Once placement is complete, it transitions to the {@link ResolvingState}.
 * </p>
 */

public class PlacingState implements GameStateLogic {

    /** How many players have already placed their totem this round. */
    private int totemPlacedCount;

    public PlacingState() { }

    /**
     * Called once per round entry.
     * Determines the placement order from TurnOrderTrack and
     * waits for each player to place their totem in turn.
     * When all players have placed, transitions to ResolvingState.
     */


    /**
     * Executes the logic for the totem placement phase.
     *
     * @param g The main game context. Must not be null.
     */
    @Override
    public void execute(Game g) {
        System.out.println("--- Entering PLACING PHASE ---");
        System.out.println("Waiting for players to place their totems...");

    }


    /**
     * Executes the logic for placing a player's totem on an offer tile.
     * <p>
     * This overrides the default interface behavior because totem placement
     * is fully legal and expected during this specific phase.
     * </p>
     *
     * @param g The main game context.
     * @param p The player placing the totem.
     * @param t The target offer tile.
     */
    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {

        t.placeTotem(p);

        System.out.println(p.getNickname() + " placed a totem on tile " + t.getId());

        if (g.getBoard().allTotemsPlaced()) {
            g.changeState(new ResolvingState());
        }
    }


    @Override
    public GameState getStateId() { return GameState.PLACING_TOTEMS; }

}

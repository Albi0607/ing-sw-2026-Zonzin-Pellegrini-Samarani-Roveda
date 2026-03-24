package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.enums.GameState;


/**
 * Base interface for implementing the State Pattern in the game engine.
 * <p>
 * It defines the contract for all concrete states (e.g., SetupState, PlacingState).
 * Each class implementing this interface encapsulates the specific logic
 * of a single phase of the game.
 * </p>
 */

public interface GameStateLogic {


    /**
     * Executes the main logic associated with the current state.
     * <p>
     * This method is invoked by the {@link Game} class
     * to delegate the execution of the current phase's rules.
     * Inside this method, the concrete state can modify the game data
     * and, if the end-of-phase conditions are met,
     * call {@code g.changeState(...)} to advance the game.
     * </p>
     *
     * @param g The main game instance the state must operate on.
     * It provides access to the boards, players, and decks.
     * Must not be null.
     */
    void execute(Game g);

    /**
     * Identifies the current state using the GameState enum.
     * <p>
     * Useful for testing, UI updates, and controller validations
     * without breaking polymorphism or using instanceof.
     * </p>
     *
     * @return The {@link GameState} enum value corresponding to this state.
     */
    GameState getStateId();


    /**
     * Attempts to place a player's totem on an offer tile.
     * <p>
     * By default, this action is not allowed and will throw an exception.
     * Only specific states (e.g., {@link PlacingState}) that allow this action
     * should override this method to provide the actual implementation.
     * </p>
     *
     * @param g The main game context. Must not be null.
     * @param p The player attempting to place the totem.
     * @param t The offer tile where the totem should be placed.
     * @throws IllegalStateException if the action is not allowed in the current state.
     */


    default void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException(
                "Cannot place totem in state: " + getStateId()
        );
    }
}

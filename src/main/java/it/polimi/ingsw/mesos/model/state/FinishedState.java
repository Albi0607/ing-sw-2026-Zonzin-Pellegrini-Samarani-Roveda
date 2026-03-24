package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.GameState;
/**
 * Represents the terminal phase of the game.
 * <p>
 * In this state, the game has officially concluded (usually after Era III).
 * The system calculates the final scores, applies any end-game effects
 * (such as {@link }), and determines the winner.
 * No further gameplay actions can be taken by the players.
 * </p>
 */

public class FinishedState implements GameStateLogic {

    /**
     * Fully automatic — no player input required.
     *
     * Adds end-game PP to each player in this order:
     * 1. PP from Builder cards (printed value on each Builder).
     * 2. PP from Inventors: numInventors × distinctInventionIconCount.
     * 3. PP from Artists: 10 PP per every 2 Artists.
     * 4. PP from Building cards: base victoryPoints + END_GAME building effects.
     *
     * Calls game.notifyBuildingEffects(TriggerType.END_GAME, null) to let
     * buildings apply their own end-game scoring effects.
     *
     * Winner = player with highest PP.
     * Tiebreaker: most food.
     * Further tie: shared victory.
     *
     * Does not transition to any further state.
     */



    /**
     * Executes the final scoring and game termination logic.
     *
     * @param g The main game context. Must not be null.
     */
    @Override
    public void execute(Game g) {
        System.out.println("--- Entering FINISHED PHASE ---");
        System.out.println("The game is over! Calculating final scores...");

    }

    @Override
    public GameState getStateId() { return GameState.FINISHED; } // probabile da cambiare
}

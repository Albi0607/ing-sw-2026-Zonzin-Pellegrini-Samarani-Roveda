package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.GameState;

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
    @Override
    public void execute(Game game) { }

    @Override
    public GameState getStateId() { return GameState.FINISHED; } // probabile da cambiare
}

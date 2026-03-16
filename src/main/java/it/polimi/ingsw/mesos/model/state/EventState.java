package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.GameState;

public class EventState implements GameStateLogic {

    /**
     * Fully automatic — no player input required.
     *
     * Sequence:
     * 1. Collect all EventCards from the lower row.
     * 2. Sort them: non-Sustenance events first (in Era order if duplicates),
     *    SustenanceEvent always last.
     * 3. Resolve each event by calling event.resolve(game)
     * 4. Discard all Character and Event cards from lower row
     *    (Building cards stay).
     * 5. Move remaining Character and Event cards from upper row to lower row
     *    (Building cards stay in upper row).
     * 6. Refill upper row by drawing (numPlayers + 4) cards from TribeDeck.
     * 7. Check if any drawn card belongs to a new Era → call game.handleEraTransition().
     * 8. If TribeDeck is now empty → this was round 10 → resolve any remaining
     *    events in upper row as well, then transitions to FinishedState.
     * 9. Otherwise transitions back to PlacingState for the next round.
     */

    // non chiaro chi deve risolvere l'evento se la carta evento o se lo stato e dove vada messa l'implementazione
    // della risoluzione dei 4 eventi -> alberto
    @Override
    public void execute(Game game) { }

    @Override
    public GameState getStateId() { return GameState.END_ROUND; } // probabile da cambiare
}

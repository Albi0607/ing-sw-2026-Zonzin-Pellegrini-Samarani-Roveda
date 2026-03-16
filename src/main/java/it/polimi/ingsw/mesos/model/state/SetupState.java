package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;

public class SetupState implements GameStateLogic {

    /**
     * Initializes the game:
     * - filters and sorts OfferTiles by player count
     * - builds and shuffles TribeDeck (Era I on top, FinalEvents at bottom)
     * - randomly chooses buildings and builds the BuildingDeck, distributes correct scores by era/number of players,
     *   deals correct counts per era/player count
     * - deals initial food to players (1st→2, 2nd/3rd→3, 4th/5th→4)
     * - places Era I building cards in upper row
     * - draws initial upper and lower rows
     * - randomly places totems on TurnOrderTrack
     * - transitions to PlacingState
     */
    @Override
    public void execute(Game game) { }
}

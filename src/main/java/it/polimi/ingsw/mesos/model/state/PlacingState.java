package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;

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
    @Override
    public void execute(Game game) { }

    /**
     * Invoked by the controller when a player chooses a tile.
     * Validates that:
     * - it is this player's turn to place
     * - the chosen tile is free and available for the current player count
     * Places the totem and increments totemPlacedCount.
     * When totemPlacedCount == numPlayers, triggers transition.
     */
    public void placeTotem(Game game, Player player, OfferTile tile) { }

    /** Returns the player whose turn it is to place, based on TurnOrderTrack. */
    public Player getCurrentPlacer(Game game) { return null; }

    public int getTotemPlacedCount() { return 0; }
}

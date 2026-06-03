package it.polimi.ingsw.mesos.model.board;

/**
 * Factory class responsible for creating and initializing a {@link TurnOrderTrack}
 * configured for the given number of players.
 *
 * <p>The turn order track defines the bonus values assigned to each position in the
 * turn order. Each player occupies one slot, and the values vary depending on the
 * total number of players in the game.
 */

public class CreateTurnOrderTrack {

    /**
     * Creates and returns a {@link TurnOrderTrack} initialized with the bonus values
     * appropriate for the given number of players.
     *
     * @param numPlayers the number of players in the current game session;
     *                   must be between 2 and 5 (inclusive)
     * @return a {@link TurnOrderTrack} configured with the correct slot values
     *         for the given player count
     * @throws IllegalArgumentException if {@code numPlayers} is less than 2 or greater than 5
     * @throws IllegalStateException    if {@code numPlayers} is within range but does not
     *                                  match any defined case (should never occur in practice)
     */
    public TurnOrderTrack initializeTurnOrderTrack(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        switch (numPlayers) {
            case 2:
                return new TurnOrderTrack(new int[]{1, -1});
            case 3:
                return new TurnOrderTrack(new int[]{2, 0, -1});
            case 4:
                return new TurnOrderTrack(new int[]{2, 1, 0, -1});
            case 5:
                return new TurnOrderTrack(new int[]{3, 1, 0, 0, -1});
            default:
                throw new IllegalStateException("Unexpected value: " + numPlayers);
        }
    }
}

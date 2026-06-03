package it.polimi.ingsw.mesos.model.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Factory class responsible for creating and initializing {@link OfferTile} instances
 * based on the number of players in the game.
 *
 * <p>Each offer tile has a unique identifier, a minimum player threshold, and various
 * resource bonuses (upper count, lower count, and food bonus). Only tiles whose minimum
 * player requirement is met by the current player count are included in the final list.
 */

public class CreateOfferTile {

    /**
     * Initializes and returns the list of {@link OfferTile} objects available for the
     * given number of players.
     *
     * <p>All predefined tiles (A through G) are evaluated against {@code numPlayers}.
     * A tile is included in the result only if its minimum player requirement
     * ({@link OfferTile#getMinPlayers()}) is less than or equal to {@code numPlayers}.
     *
     * @param numPlayers the number of players in the current game session;
     *                   must be between 2 and 5 (inclusive)
     * @return a {@link List} of {@link OfferTile} instances valid for the given player count,
     *         in the order they are defined
     * @throws IllegalArgumentException if {@code numPlayers} is less than 2 or greater than 5
     */

    public List<OfferTile> initializeOfferTiles(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        List<OfferTile> tiles = new ArrayList<>();

        OfferTile tileA = new OfferTile('A', 5);
        tileA.setUpperCount(0); tileA.setLowerCount(0); tileA.setFoodBonus(3);

        OfferTile tileB = new OfferTile('B', 0);
        tileB.setUpperCount(0); tileB.setLowerCount(1); tileB.setFoodBonus(0);

        OfferTile tileC = new OfferTile('C', 0);
        tileC.setUpperCount(1); tileC.setLowerCount(0); tileC.setFoodBonus(0);

        OfferTile tileD = new OfferTile('D', 3);
        tileD.setUpperCount(0); tileD.setLowerCount(2); tileD.setFoodBonus(0);

        OfferTile tileE = new OfferTile('E', 0);
        tileE.setUpperCount(1); tileE.setLowerCount(1); tileE.setFoodBonus(0);

        OfferTile tileF = new OfferTile('F', 0);
        tileF.setUpperCount(2); tileF.setLowerCount(0); tileF.setFoodBonus(0);

        OfferTile tileG = new OfferTile('G', 4);
        tileG.setUpperCount(2); tileG.setLowerCount(1); tileG.setFoodBonus(0);

        List<OfferTile> allTiles = Arrays.asList(
                tileA, tileB, tileC, tileD, tileE, tileF, tileG
        );

        for (OfferTile t : allTiles) {
            if (t.getMinPlayers() <= numPlayers) {
                tiles.add(t);
            }
        }

        return tiles;
    }
}


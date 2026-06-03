/**
 * Represents an offer tile in the game, which allows a player to select the action they want to take.
 *
 * Main fields:
 * - upperCount and lowerCount: indicate how many cards the player can take from the upper and lower rows
 *   when occupying this tile.
 * - foodBonus: the amount of food the player gains by placing a token on this tile.
 * - id: a character identifier associated with the tile.
 * - minPlayers: the minimum number of players required for this tile to be available.
 * - host: the player who has occupied this tile; null if the tile is currently available.
 */
package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Player;

public class OfferTile {
    private int upperCount;
    private int lowerCount;
    private int foodBonus;
    private char id;
    private int minPlayers;
    private Player host;

    /**
     * Constructs an OfferTile with the specified identifier and minimum number of players.
     *
     * @param id         the character identifier of the tile
     * @param minPlayers the minimum number of players required for this tile to be available
     */
    public OfferTile(char id, int minPlayers) {
        this.id = id;
        this.minPlayers = minPlayers;
    }

    /**
     * Grants the food bonus associated with this tile to the specified player.
     *
     * @param p the {@link Player} who receives the food bonus
     */
    public void giveFoodBonus(Player p) {
        p.addFood(this.foodBonus);
    }

    /**
     * Places the specified player's token on this tile, marking it as occupied.
     *
     * @param player the {@link Player} placing their token on this tile
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws IllegalStateException    if this tile is already occupied by another player
     */
    public void placeTotem(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        if (host != null) {
            throw new IllegalStateException("This tile is already occupied");
        }

        host = player;
    }

    /**
     * Resets this tile by removing the current host, making it available again
     * for the next round.
     */
    public void reset() {
        host = null;
    }

    // Setters
    public void setUpperCount(int upperCount) {
        this.upperCount = upperCount;
    }
    public void setLowerCount(int lowerCount) {
        this.lowerCount = lowerCount;
    }
    public void setFoodBonus(int foodBonus) {
        this.foodBonus = foodBonus;
    }
    public void setHost(Player host) {
        this.host = host;
    }

    // Getters
    public int getUpperCount() { return upperCount; }
    public int getLowerCount() { return lowerCount; }
    public int getFoodBonus() { return foodBonus; }
    public int getMinPlayers() { return minPlayers; }
    public Player getHost() { return host; }
    public char getId() { return id; }
    /**
     * Checks if this tile is currently available
     * @return true if the tile is available, false otherwise
     */
    public boolean isAvailable() {
        return host == null;
    }
}
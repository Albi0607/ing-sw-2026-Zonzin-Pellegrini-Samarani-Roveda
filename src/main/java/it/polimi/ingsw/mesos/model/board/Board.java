/**
 * Represents the game board.
 *
 * This class manages the rows of cards, divided into upper and lower rows, the decks of tribe and building cards,
 * the offer tiles, and the turn order of the players.
 * It serves as the central structure for running the game.
 *
 * Main fields:
 * - turnOrderTrack: keeps track of the players' turn order.
 * - upperRow, lowerRow: the two visible rows of cards on the board.
 * - tribeDeck: deck of tribe cards containing all the available tribe cards for the game.
 * - buildingDecks: deck of building cards
 * - tiles: list of available offer tiles, adjusted based on the number of active players.
 */
package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.Era;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Board {

    private TurnOrderTrack turnOrderTrack;
    private List<Card> upperRow;
    private List<Card> lowerRow;
    private Deck<TribeCard> tribeDeck;
    private Map<Era, Deck<BuildingCard>> buildingDecks;
    private List<OfferTile> tiles;

    public Board(int numPlayers) {
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.tiles = new ArrayList<>();
    }

    /**
     * Fills the upper row with cards drawn from the provided deck.
     * The number of cards added is determined by the target size.
     *
     * @param deck the deck to draw cards from
     * @param targetSize the desired number of cards in the upper row
     */
    public void refillRows(Deck<TribeCard> deck, int targetSize) {
        // Calculate how many cards are needed to reach the target size
        int cardsNeeded = targetSize - upperRow.size();

        // If the row is already full or the deck is empty, do nothing

        if (cardsNeeded <= 0) {
            return;
        }

        // capire se va lanciato da qui il fatto che il gioco è finito
        if (deck.isEmpty()) {
            return;
        }

        // Draw cards from the deck until the row reaches the target size
        // or the deck runs out of cards
        while (upperRow.size() < targetSize && !deck.isEmpty()) {
            TribeCard card = deck.draw();
            upperRow.add(card);
        }
    }

    /** Move all non-Building cards from upperRow to lowerRow. */
    public void shiftUpperToLower() {
        upperRow.removeIf(card -> {
            if (!(card instanceof BuildingCard)) {
                lowerRow.add(card);
                return true;
            }
            return false;
        });
    }

    /** Remove all Character and Event cards from lowerRow. */
    public void clearLowerRow() {
        lowerRow.removeIf(card -> !(card instanceof BuildingCard));
    }

    /** Remove all Building cards from lowerRow (used at Era III start). */
    public void clearBuildingsFromLower() {
        lowerRow.removeIf(card -> card instanceof BuildingCard);
    }

    /** Move Building cards from upperRow to lowerRow. */
    public void shiftBuildingsToLower() {
        upperRow.removeIf(card -> {
            if (card instanceof BuildingCard) {
                lowerRow.add(card);
                return true;
            }
            return false;
        });
    }

    /**
     * Allows a player to select and take a card from the lower row of the board.
     *
     * @param index the position of the card in the lower row
     * @return the Card that was removed from the lower row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Card takeCardFromLower(int index) {
        if (index < 0 || index >= lowerRow.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return lowerRow.remove(index);
    }

    /**
     * Allows a player to select and take a card from the upper row of the board.
     *
     * @param index the position of the card in the upper row
     * @return the Card that was removed from the upper row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Card takeCardFromUpper(int index) {
        if (index < 0 || index >= upperRow.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return upperRow.remove(index);
    }

    // --- Getters ---

    public TurnOrderTrack getTurnOrderTrack() {
        return turnOrderTrack;
    }

    public List<Card> getUpperRow() {
        return upperRow;
    }

    public List<Card> getLowerRow() {
        return lowerRow;
    }

    public List<OfferTile> getTiles() {
        return tiles;
    }

    public OfferTile getTile(char id) {
        if (tiles == null) return null;

        for (OfferTile tile : tiles) {
            if (tile.getId() == id) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Returns only the tiles that are currently available for use.
     * A tile is considered available if it does not have any totem placed on top.
     *
     * @return a list of tiles that meet these availability conditions
     */
    public List<OfferTile> getAvailableTiles() {
        List<OfferTile> available = new ArrayList<>();

        if (tiles == null) return available;

        for (OfferTile tile : tiles) {
            if (tile.isAvailable()) {
                available.add(tile);
            }
        }

        return available;
    }


    // crezione delle offer tiles
    public void initializeOfferTiles(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Invalid number of players");
        }
        tiles = new ArrayList<>();

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

        List<OfferTile> allTiles = new ArrayList<>(
                Arrays.asList(tileA, tileB, tileC, tileD, tileE, tileF, tileG)
        );

        for (OfferTile t : allTiles) {
            if (t.getMinPlayers() <= numPlayers) {
                tiles.add(t);
            }
        }
    }

    // crezione delle turn order
    public void initializeTurnOrderTrack(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        switch (numPlayers) {
            case 2:
                this.turnOrderTrack = new TurnOrderTrack(new int[]{1, -1});
                break;
            case 3:
                this.turnOrderTrack = new TurnOrderTrack(new int[]{2, 0, -1});
                break;
            case 4:
                this.turnOrderTrack = new TurnOrderTrack(new int[]{2, 1, 0, -1});
                break;
            case 5:
                this.turnOrderTrack = new TurnOrderTrack(new int[]{3, 1, 0, 0, -1});
                break;
        }
    }
}

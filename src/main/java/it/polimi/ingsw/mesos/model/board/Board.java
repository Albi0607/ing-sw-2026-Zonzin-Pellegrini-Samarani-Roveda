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

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.BuildingDeckStrategy;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.deck.TribeDeckStrategy;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private TurnOrderTrack turnOrderTrack;
    private final List<Card> upperRow;
    private final List<Card> lowerRow;
    private Deck<TribeCard> tribeDeck;
    private Deck<BuildingCard> buildingDecks;
    private List<OfferTile> tiles;

    public Board(int numPlayers) {
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.tribeDeck = new Deck<>(numPlayers, new TribeDeckStrategy());
        this.buildingDecks = new Deck<>(numPlayers, new BuildingDeckStrategy());

    }

    /**
     * Refills the upper row with cards from the tribe deck and manages era transitions.
     * <p>
     * This method draws cards from the tribe deck until the upper row reaches the {@code targetSize}
     * or the deck is empty.
     * </p>
     * <p>
     * Era transitions are detected and triggered in two scenarios:
     * <ul>
     * <li><b>During refill:</b> If a drawn card belongs to an era later than the current game era.</li>
     * <li><b>After refill:</b> If the card remaining on top of the deck belongs to a later era,
     * simulating the visibility of the next era on the deck's back.</li>
     * </ul>
     * </p>
     *
     * @param targetSize The total number of tribe cards required in the upper row.
     * @param g          The current {@link Game} instance used to handle era transitions and state.
     */
    public void refillRows(int targetSize, Game g) {
        if (this.tribeDeck.isEmpty()) return;

        int tribeCardsDrawn = 0;

        // --- Durante il riempimento ---
        while (tribeCardsDrawn < targetSize && !this.tribeDeck.isEmpty()) {
            TribeCard card = this.tribeDeck.draw();

            if (card.getEra().ordinal() > g.getCurrentEra().ordinal()) {
                System.out.println("Cambio Era rilevato sulla carta appena pescata!");
                g.handleEraTransition(card.getEra());
            }


            upperRow.add(0, card);
            tribeCardsDrawn++;
        }

        //  --- Dopo il riempimento verifichiamo l'era della carta in cima al mazzo ---
        if (!this.tribeDeck.isEmpty()) {
            TribeCard nextOnDeck = this.tribeDeck.draw(); // verifichiamo l'era della carta in cima al mazzo

            if (nextOnDeck.getEra().ordinal() > g.getCurrentEra().ordinal()) {
                System.out.println("Era successiva visibile sul dorso del mazzo!");
                g.handleEraTransition(nextOnDeck.getEra());
            }

            // La rimettiamo a posto in cima al mazzo
            this.tribeDeck.put(nextOnDeck);
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
        this.lowerRow.clear();
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

    public Deck<TribeCard> getTribeDeck() {
        return this.tribeDeck;
    }

    public Deck<BuildingCard> getBuildingDeck() { return this.buildingDecks; }

    public void setTurnOrderTrack(TurnOrderTrack turnOrderTrack) {
        if (turnOrderTrack == null) {
            throw new IllegalArgumentException("TurnOrderTrack cannot be null");
        }
        this.turnOrderTrack = turnOrderTrack;
    }

    public void setTiles(List<OfferTile> tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("Offer tiles cannot be null");
        }
        this.tiles = tiles;
    }
}

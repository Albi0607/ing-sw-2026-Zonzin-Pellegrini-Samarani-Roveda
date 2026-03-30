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
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.deck.BuildingDeckStrategy;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.deck.TribeDeckStrategy;
import it.polimi.ingsw.mesos.model.enums.Era;

import java.util.ArrayList;
import java.util.Arrays;
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
     * Fills the upper row with cards drawn from the provided deck.
     * The number of cards added is determined by the target size.
     *
     * @param targetSize the desired number of cards in the upper row
     */
    public void refillRows(int targetSize, Game g) {
        if (this.tribeDeck.isEmpty()) return;


        int tribeCardsDrawn = 0;
        boolean eraChanged = false;
        Era newEraDetected = g.getCurrentEra();

        while (tribeCardsDrawn < targetSize && !this.tribeDeck.isEmpty()) {
            TribeCard card = this.tribeDeck.draw();

            if (card.getEra().ordinal() > g.getCurrentEra().ordinal()) {
                eraChanged = true;
                newEraDetected = card.getEra();
            }

            upperRow.add(card);
            tribeCardsDrawn++;
        }

        // SOLO ORA, se è stato rilevato un cambio, scatta la transizione degli edifici
        if (eraChanged) {
            g.handleEraTransition(newEraDetected);
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

    public List<EventCard> getEvents() {
        List<EventCard> events = new ArrayList<>();

        for (Card card : this.lowerRow) {
            // Chiediamo alla carta di "trasformarsi" in evento
            EventCard event = card.getAsEventCard();

            // Se non è null, significa che era davvero un EventCard!
            if (event != null) {
                events.add(event);
            }
        }

        return events;
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

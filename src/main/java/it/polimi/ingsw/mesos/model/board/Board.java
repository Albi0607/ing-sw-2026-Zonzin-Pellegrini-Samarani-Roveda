package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.Era;

import java.util.List;
import java.util.Map;

public class Board {

    private TurnOrderTrack turnOrderTrack;
    private List<Card> upperRow;
    private List<Card> lowerRow;
    private Deck<TribeCard> tribeDeck;
    private Map<Era, Deck<BuildingCard>> buildingDecks;
    private List<OfferTile> tiles;

    public Board(int numPlayers) { }

    /** Draw cards from deck and fill both rows to their target sizes. */
    public void refillRows(Deck<TribeCard> deck, int targetSize) { }

    /** Move all non-Building cards from upperRow to lowerRow. */
    public void shiftUpperToLower() { }

    /** Remove all Character and Event cards from lowerRow. */
    public void clearLowerRow() { }

    /** Remove all Building cards from lowerRow (used at Era III start). */
    public void clearBuildingsFromLower() { }

    /** Move Building cards from upperRow to lowerRow. */
    public void shiftBuildingsToLower() { }

    public Card takeCardFromLower(int index) { return null; }

    public Card takeCardFromUpper(int index) { return null; }

    // --- Getters ---


    public TurnOrderTrack getTurnOrderTrack() { return null; }

    public List<Card> getUpperRow() { return null; }

    public List<Card> getLowerRow() { return null; }

    //vecchi metodi di OfferTrack non è detto che servano tutti

    public List<OfferTile> getTiles() { return null; }

    public OfferTile getTile(char id) { return null; }

    /** Returns only tiles available for the current player count and without a totem. */
    public List<OfferTile> getAvailableTiles() { return null; }
}

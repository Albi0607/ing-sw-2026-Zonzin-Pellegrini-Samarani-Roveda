package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.Deck;

import java.util.List;

public class Board {

    private OfferTrack offerTrack;
    private TurnOrderTrack turnOrderTrack;
    private List<Card> upperRow;
    private List<Card> lowerRow;

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

    public OfferTrack getOfferTrack() { return null; }

    public TurnOrderTrack getTurnOrderTrack() { return null; }

    public List<Card> getUpperRow() { return null; }

    public List<Card> getLowerRow() { return null; }
}

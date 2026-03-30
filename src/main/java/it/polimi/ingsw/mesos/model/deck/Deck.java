package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.Card;

import java.util.*;



/**Generic deck class used to represent the two types of decks: the game deck containing all tribe cards and the
 * building deck containing the usable building cards.
 The deck is implemented as a stack in order to draw one card at a time in era order
 @author Alberto Roveda
 */
public class Deck<T extends Card> {
    /**Deck attribute containing the cards to be used during the game; depending on how this class is instantiated,
     * it can represent either a tribe deck or a building deck*/
    private Stack<T> deck;

    /**Constructor of this class that builds the decks based on the number of players and the deck creation strategy,
     * whether it is a tribe deck or a building deck
     * @param numPlayers the number of player of the game
     * @param strategy differentiates the creation of the tribe deck from the building one*/
    public Deck(int numPlayers, CreateStrategy<T> strategy){
        this.deck = strategy.createDeck(numPlayers);
    }

    // costruttore solo per il test
    public Deck(Stack<T> manualStack) {
        this.deck = manualStack;
    }

    /**differentiates the creation of the tribe deck from the building one
     * Method that draws one card at a time from the deck
     * @return
     */
    public T draw() {
        if (this.isEmpty()){return null;}
        return deck.pop();
    }

    /**Method to check whether the deck is empty
     *
     * @return true if is empty, false if there are other cards
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**Method to place a card on top of the deck; generally not recommended for use, intended only for building cards
     * to allow drawing until a building of the next era is found and then placing it back on top of the deck
     * @param card the card to put in the deck*/
    public void put(T card){
        deck.push(card);
    }

    /**Method that returns the size of the deck
     * @return number of cards remaining in the deck*/
    public int size() {
            return deck.size();
    }
}

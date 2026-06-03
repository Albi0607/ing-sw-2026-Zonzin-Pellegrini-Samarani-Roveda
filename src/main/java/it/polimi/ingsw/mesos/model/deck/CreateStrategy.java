package it.polimi.ingsw.mesos.model.deck;
import it.polimi.ingsw.mesos.model.card.Card;

import java.util.Stack;

/**
 * Interface with a single method to implement deck creation, specifically the game deck with character cards and
 * event cards, and the building deck
 */
public interface CreateStrategy<T extends Card> {

    /**
     * Creates a deck of cards based on the specific implementation.
     *
     * This method must be implemented by concrete strategies and is responsible
     * for generating the appropriate deck depending on the context, such as
     * creating a building deck or a tribe deck.
     *
     * @param numPlayers the number of players, used to adjust the deck composition
     * @return a stack representing the generated deck of cards
     */
    Stack<T> createDeck(int numPlayers);
}

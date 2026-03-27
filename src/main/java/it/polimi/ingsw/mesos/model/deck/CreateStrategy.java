package it.polimi.ingsw.mesos.model.deck;
import it.polimi.ingsw.mesos.model.card.Card;

import java.util.Stack;

/**Interface with a single method to implement deck creation, specifically the game deck with character cards and
 * event cards, and the building deck
 * @author Alberto Roveda*/
public interface CreateStrategy<T extends Card> {
    Stack<T> createDeck(int numPlayers);
}

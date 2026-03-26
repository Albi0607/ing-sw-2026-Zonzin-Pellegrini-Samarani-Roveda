package it.polimi.ingsw.mesos.model.deck;
import it.polimi.ingsw.mesos.model.card.Card;

import java.util.Stack;

public interface CreateStrategy<T extends Card> {
    Stack<T> createDeck(int numPlayers);
}

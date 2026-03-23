package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.card.Card;

import java.util.List;
import java.util.Stack;

public class Deck<TribeCard> {

    private Stack<TribeCard> cards;

    public Deck() { }

    public Deck(List<TribeCard> cards) { }

    public void shuffle() { }

    public TribeCard draw() {
        if (this.isEmpty()){return null;}
        return cards.pop();
    }


    public boolean isEmpty() {
        return this.size() == 0;
        }


    public int size() { return 0; }
}

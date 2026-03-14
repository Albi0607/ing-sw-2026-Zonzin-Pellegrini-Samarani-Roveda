package it.polimi.ingsw.mesos.model.deck;

import java.util.List;
import java.util.Stack;

public class Deck<T> {

    private Stack<T> cards;

    public Deck() { }

    public Deck(List<T> cards) { }

    public void shuffle() { }

    public T draw() { return null; }

    public List<T> drawN(int n) { return null; }

    public void push(T item) { }

    public boolean isEmpty() { return false; }

    public int size() { return 0; }
}

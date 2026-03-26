package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;

import java.util.*;



// Classe deck che rappresenta il mazzo di gioco. Bisogna chiamarla dalla board passando come parametro il numero di giocatori
// la classe restituisce un oggetto deck il quale inizializza il mazzo mischiando in cima le carte di era_I poi le carte di
// era_II e infine le carte di era_III mettendo per ultime le due carte evento isFinal. Il mazzo non va ulteriormente toccato
// ma bisogna prendere carta per carta dalla cima fino in fondo con il metodo draw fino all'esaurimento delle carte.
public class Deck<T extends Card> {

    private Stack<T> deck;


    public Deck(int numPlayers, CreateStrategy<T> strategy){
        this.deck = strategy.createDeck(numPlayers);
    }


    public T draw() {
        if (this.isEmpty()){return null;}
        return deck.pop();
    }


    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void put(T card){
        deck.push(card);
    }


    public int size() {
            return deck.size();
    }
}

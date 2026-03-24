package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.Era;

import java.util.*;

public class Deck<T> {

    private Stack<TribeCard> cards;

    public Deck(int numPlayers) {
        this.cards = new Stack<>();

        List<CharacterCard> cCard = new CreateCharacterCard().getAllCharacterCards();
        List<EventCard> eCard = new CreateEventCard().getAllEventCards();

        List<TribeCard> deck = new ArrayList<>();
        List<EventCard> finalCards = new ArrayList<>();
        List<EventCard> noFinalCards = new ArrayList<>();

        //divido le carte evento finali da non finali così so quali sono le ultime 2 carte da piazzare in fondo al mazzo
        for(EventCard e : eCard){
            if(e.isFinal()) finalCards.add(e);
            else noFinalCards.add(e);
        }

        for(Era era: Era.values()){
            List<TribeCard> eraCards = new ArrayList<>();
            for(CharacterCard c : cCard){
                if(c.getEra()==era && c.getPlayerRequired()<=numPlayers) eraCards.add(c);
            }

            for(EventCard e : noFinalCards){
                if(e.getEra()==era) eraCards.add(e);
            }

            Collections.shuffle(eraCards);
            deck.addAll(eraCards);
        }

        deck.addAll(finalCards);

        Collections.reverse(deck);
        this.cards.addAll(deck);

    }

    public TribeCard draw() {
        if (this.isEmpty()){return null;}
        return cards.pop();
    }


    public boolean isEmpty() {
        return this.size() == 0;
        }


    public int size() {
            return cards.size();
    }
}

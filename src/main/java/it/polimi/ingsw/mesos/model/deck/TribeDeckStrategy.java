package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.Era;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**Class that overrides the method of the corresponding interface to create the main game deck in era order,
 * placing the two isFinal cards at the end of the deck, and returning a stack
 * @author Alberto Roveda*/
public class TribeDeckStrategy implements CreateStrategy<TribeCard>{

    /**
     * Implementation of the method to create the deck, building the main tribe deck
     * @param numPlayers number of players used to manage the variation of cards in the deck
     * @return a stack representing the deck from which cards are drawn during the game
     */
    @Override
    public  Stack <TribeCard> createDeck(int numPlayers) {

        List<CharacterCard> cCard = new CreateCharacterCard("characters.json").getAllCharacterCards();
        List<EventCard> eCard = new CreateEventCard("event.json").getAllEventCards();

        List<TribeCard> tempDeck = new ArrayList<>();
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
            tempDeck.addAll(eraCards);
        }

        tempDeck.addAll(finalCards);

        Collections.reverse(tempDeck);
        Stack <TribeCard> result = new Stack<>();
        result.addAll(tempDeck);
        return result;
    }
}

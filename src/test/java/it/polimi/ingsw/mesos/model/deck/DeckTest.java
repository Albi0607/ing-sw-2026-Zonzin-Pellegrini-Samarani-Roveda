package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.character.Gatherer;
import it.polimi.ingsw.mesos.model.card.character.Hunter;
import it.polimi.ingsw.mesos.model.card.event.*;
import it.polimi.ingsw.mesos.model.enums.Era;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
    @Test
    void testCorrectNumberOfCardInDeck(){
        Deck<TribeCard> deck = new Deck<>(5);
        assertEquals(96,deck.size());
    }

    @Test
    void testPresenceOfGatherer(){
        Deck<TribeCard> deck = new Deck<>(5);
        Gatherer gatherer = new Gatherer(Era.ERA_I,3);
        boolean flag = false;

        while(!deck.isEmpty()){
            Card c = deck.draw();
            if(c instanceof Gatherer){
                if (c.getEra()==gatherer.getEra() && ((Gatherer) c).getPlayerRequired()==gatherer.getPlayerRequired())
                    flag = true;
                break;
            }
        }

        assertTrue(flag);
    }

    @Test
    void testPresenceOfHunter(){
        Deck<TribeCard> deck = new Deck<>(5);
        Hunter hunter = new Hunter(Era.ERA_I,2,true);
        boolean flag = false;

        while(!deck.isEmpty()){
            Card c = deck.draw();
            if(c instanceof Hunter){
                if (   c.getEra()==hunter.getEra() &&
                        ((Hunter) c).getPlayerRequired()==hunter.getPlayerRequired() &&
                        ((Hunter) c).hasIcon()==hunter.hasIcon()) {
                    flag = true;
                    break;
                }
            }
        }

        assertTrue(flag);
    }

    @Test
    void testNoPresenceOfHunter(){
        Deck<TribeCard> deck = new Deck<>(5);
        Hunter hunter = new Hunter(Era.ERA_I,3,true);
        boolean flag = false;

        while(!deck.isEmpty()){
            Card c = deck.draw();
            if(c instanceof Hunter){
                if (   c.getEra()==hunter.getEra() &&
                        ((Hunter) c).getPlayerRequired()==hunter.getPlayerRequired() &&
                        ((Hunter) c).hasIcon()==hunter.hasIcon()) {
                    flag = true;
                    break;
                }
            }
        }

        assertFalse(flag);
    }


    @Test
    void presenceOf3HuntEventCard(){
        Deck<TribeCard> deck = new Deck<>(2);
        int count = 0;
        while(!deck.isEmpty()){
            Card e = deck.draw();
            if(e instanceof HuntEvent){
                count ++;
            }
        }

        assertEquals(3,count);
    }

    @Test
    void presenceOf3CavePaintingEvent(){
        Deck<TribeCard> deck = new Deck<>(2);
        int count = 0;
        while(!deck.isEmpty()){
            Card e = deck.draw();
            if(e instanceof CavePaintingEvent){
                count ++;
            }
        }

        assertEquals(3,count);
    }

    @Test
    void presenceOf3SustenanceEvent(){
        Deck<TribeCard> deck = new Deck<>(2);
        int count = 0;
        while(!deck.isEmpty()){
            Card e = deck.draw();
            if(e instanceof SustenanceEvent){
                count ++;
            }
        }

        assertEquals(3,count);
    }

    @Test
    void presenceOf3ShamanicRitualEvent(){
        Deck<TribeCard> deck = new Deck<>(2);
        int count = 0;
        while(!deck.isEmpty()){
            Card e = deck.draw();
            if(e instanceof ShamanicRitualEvent){
                count ++;
            }
        }

        assertEquals(3,count);
    }

    @Test
    void testLastsFinalCards(){
        Deck<TribeCard> deck = new Deck<>(5);
        List<TribeCard> list = new ArrayList<>();
        while(!deck.isEmpty()){
            list.add(deck.draw());
        }

        EventCard card1 = (EventCard) list.get(94);
        EventCard card2 = (EventCard) list.get(95);

        assertTrue(card1.isFinal());
        assertTrue(card2.isFinal());
    }

    @Test
    void firstCardOfDeckEra_I(){
        Deck<TribeCard> deck = new Deck<>(2);
        boolean flag = false;
        if(Era.ERA_I == deck.draw().getEra()) flag = true;

        assertTrue(flag);
    }

}




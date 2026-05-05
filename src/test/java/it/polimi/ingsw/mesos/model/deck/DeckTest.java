package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.Gatherer;
import it.polimi.ingsw.mesos.model.card.character.Hunter;
import it.polimi.ingsw.mesos.model.card.event.*;
import it.polimi.ingsw.mesos.common.enums.Era;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void testCorrectNumberOfTribeCardInDeckFor5Players(){
        Deck<TribeCard> deck = new Deck<>(5,new TribeDeckStrategy());
        assertEquals(96,deck.size());
    }
    @Test
    void testCorrectNumberOfTribeCardInDeckFor4Players(){
        Deck<TribeCard> deck = new Deck<>(4,new TribeDeckStrategy());
        assertEquals(85,deck.size());
    }
    @Test
    void testCorrectNumberOfTribeCardInDeckFor3Players(){
        Deck<TribeCard> deck = new Deck<>(3,new TribeDeckStrategy());
        assertEquals(74,deck.size());
    }
    @Test
    void testCorrectNumberOfTribeCardInDeckFor2Players(){
        Deck<TribeCard> deck = new Deck<>(2,new TribeDeckStrategy());
        assertEquals(63,deck.size());
    }

    @Test
    void testWrongNumberOfPlayerInTribeDeck(){
        assertThrows(IllegalArgumentException.class,()-> new Deck<>(1,new TribeDeckStrategy()));
        assertThrows(IllegalArgumentException.class,()-> new Deck<>(6,new TribeDeckStrategy()));
    }

    @Test
    void testPresenceOfGatherer(){
        Deck<TribeCard> deck = new Deck<>(5,new TribeDeckStrategy());
        Gatherer gatherer = new Gatherer(Era.ERA_I,3,3);
        boolean flag = false;

        while(!deck.isEmpty()){
            Card c = deck.draw();
            if(c instanceof Gatherer){
                if (c.getEra()==gatherer.getEra() && ((Gatherer) c).getPlayerRequired()==gatherer.getPlayerRequired()) {
                    flag = true;
                    break;
                }
            }
        }

        assertTrue(flag);
    }

    @Test
    void testPresenceOfHunter(){
        Deck<TribeCard> deck = new Deck<>(5,new TribeDeckStrategy());
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
        Deck<TribeCard> deck = new Deck<>(5,new TribeDeckStrategy());
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
    void presenceOfHuntEventCard(){
        Deck<TribeCard> deck = new Deck<>(2,new TribeDeckStrategy());
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
    void presenceOfCavePaintingEvent(){
        Deck<TribeCard> deck = new Deck<>(3,new TribeDeckStrategy());
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
    void presenceOfSustenanceEvent(){
        Deck<TribeCard> deck = new Deck<>(4,new TribeDeckStrategy());
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
    void presenceOfShamanicRitualEvent(){
        Deck<TribeCard> deck = new Deck<>(2,new TribeDeckStrategy());
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
        Deck<TribeCard> deck = new Deck<>(5,new TribeDeckStrategy());
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
        Deck<TribeCard> deck = new Deck<>(2,new TribeDeckStrategy());
        boolean flag = false;
        if(Era.ERA_I == deck.draw().getEra()) flag = true;

        assertTrue(flag);
    }

    @Test
    void testDrawWithEmptyDeck(){
        Deck<TribeCard> deck = new Deck<>(5,new TribeDeckStrategy());
        int cont = 96;
        while(cont!=0){
            deck.draw();
            cont--;
        }

        Card c = deck.draw();

        assertNull(c);
    }

    @Test
    void testWrongNumberOfPlayerInBuildingDeck(){
        assertThrows(IllegalArgumentException.class,()-> new Deck<>(1,new BuildingDeckStrategy()));
        assertThrows(IllegalArgumentException.class,()-> new Deck<>(6,new BuildingDeckStrategy()));
    }

    @Test
    void testCorrectNumberOfBuildingCardInDeckFor5Players(){
        Deck<BuildingCard> deck = new Deck<>(5,new BuildingDeckStrategy());
        assertEquals(10,deck.size());
    }
    @Test
    void testCorrectNumberOfBuildingCardInDeckFor4Players(){
        Deck<BuildingCard> deck = new Deck<>(4,new BuildingDeckStrategy());
        assertEquals(9,deck.size());
    }
    @Test
    void testCorrectNumberOfBuildingCardInDeckFor3Players(){
        Deck<BuildingCard> deck = new Deck<>(3,new BuildingDeckStrategy());
        assertEquals(8,deck.size());
    }
    @Test
    void testCorrectNumberOfBuildingCardInDeckFor2Players(){
        Deck<BuildingCard> deck = new Deck<>(2,new BuildingDeckStrategy());
        assertEquals(6,deck.size());
    }

}




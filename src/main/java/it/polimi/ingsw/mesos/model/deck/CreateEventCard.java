package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.card.event.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CreateEventCard {
    private List<EventCard> allEventCards;

    public CreateEventCard(String path){
        loadCards(path);
    }

    public void loadCards(String path){

        try {
            List<EventCardJson> eventCardJsons = OpenFileJson.loadList(
                    path,
                    new TypeReference<List<EventCardJson>>() {}
            );

            allEventCards = new ArrayList<>();

            for(EventCardJson j : eventCardJsons){
                EventCard card = null;
                switch(j.type){
                    case SUSTENANCE:
                        card = new SustenanceEvent(
                                j.era,
                                j.playersRequired,
                                j.isFinal,
                                j.losePoints
                        );
                        break;

                    case SHAMAN_RITUAL:
                        card = new ShamanicRitualEvent(
                                j.era,
                                j.playersRequired,
                                j.isFinal,
                                j.gainPoints,
                                j.losePoints
                        );
                        break;

                    case PAINTING:
                        card = new CavePaintingEvent(
                                j.era,
                                j.playersRequired,
                                j.isFinal,
                                j.loseNumber,
                                j.gainNumber,
                                j.losePoints,
                                j.gainPoints
                        );
                        break;

                    case HUNT:
                        card = new HuntEvent(
                                j.era,
                                j.playersRequired,
                                j.isFinal,
                                j.gainPoints
                        );
                        break;
                }

                if(card != null){
                    allEventCards.add(card);
                }
            }

        } catch(IOException e){
            System.err.println("Error loading cards: " + e.getMessage());
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<EventCard> getAllEventCards() {
        return allEventCards;
    }
}



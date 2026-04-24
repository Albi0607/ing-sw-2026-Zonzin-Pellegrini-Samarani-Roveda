package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.card.event.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**Class that allows creating all event cards for the Mesos game using the appropriate JSON file
 * @author Alberto Roveda
 */

public class CreateEventCard {

    /**List containing all event cards*/
    private List<EventCard> allEventCards;

    /**Constructor of the class that creates all the cards from a JSON file, using EventCardJson as a helper class
     *  to distinguish the creation of different event cards
     *  @param path file JSON in resources with all event card
     *  */
    public CreateEventCard(String path){
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

                    default:
                        throw new IllegalArgumentException( "Unknow event type " + j.type);

                }

                card.setId(j.id);

                allEventCards.add(card);

                CardRegistry.registerCard(j.id, j);//aggiunge la carta anche al nostro database

            }

            //mi devo ricordare di gestire queste eccezioni con ramo try catch quando verrà creato il deck in main
        } catch(IOException e){
            throw new RuntimeException("Error loading cards",e);
        }

    }

    /**Method to return to the caller the list containing all the created cards
     * @return list with all cards*/
    public List<EventCard> getAllEventCards() {
        return allEventCards;
    }
}



package it.polimi.ingsw.mesos.model.card;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.Era;

/** Abstract class to represent any type of card
 * @author Alberto Roveda*/
public abstract class Card {

    /**Era attribute for all cards*/
    private final Era era;

    /**General constructor for all cards
     * @param era the era of the card*/
    public Card(Era era) {
        this.era = era;
    }

    /**Getter method to retrieve the era of the card
     * @return the era of the card*/
    public Era getEra() {
        return this.era;
    }

    public EventCard getAsEventCard() {
        return null;
    }

    // uso il polimorfismo anzichè instanceof in offertile
    public void addTo(Player player){}

    public int getCost() {return 0; }

}

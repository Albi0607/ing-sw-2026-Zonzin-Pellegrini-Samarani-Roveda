package it.polimi.ingsw.mesos.model.card;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.common.enums.Era;

/**
 * Abstract class to represent any type of card
 */
public abstract class Card {

    /**Era attribute for all cards*/
    private final Era era;

    /**
     * Unique identifier of the card.
     *
     * This ID is used to uniquely identify the card instance and to retrieve
     * the correct information from external JSON files.
     */
    protected String id;

    /**General constructor for all cards
     * @param era the era of the card*/
    public Card(Era era) {
        this.era = era;
    }

    /**
     * Returns the unique identifier of the card.
     *
     * @return the card identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the card.
     *
     * @param id the identifier to assign to the card
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter method to retrieve the era of the card
     *
     * @return the era of the card
     */
    public Era getEra() {
        return this.era;
    }

    /**
     * Returns this card as an EventCard if applicable.
     *
     * @return the EventCard representation of this card, or null if not applicable
     */
    public EventCard getAsEventCard() {
        return null;
    }

    /**
     * Adds this card to the player's tribe.
     *
     * This method is intended to be overridden by subclasses to implement
     * specific behavior depending on the card type.
     *
     * @param player the player to which the card is applied
     */
    public void addTo(Player player){}

    /**
     * Returns the cost of the card.
     *
     * @return the cost value (default is 0 for this base implementation)
     */
    public int getCost() {return 0; }

}

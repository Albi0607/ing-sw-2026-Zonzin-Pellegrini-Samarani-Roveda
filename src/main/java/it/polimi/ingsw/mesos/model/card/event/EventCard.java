package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.common.enums.Era;
import it.polimi.ingsw.mesos.common.enums.EventType;

/**Abstract class to define all event classes
 * @author Alberto Roveda
 */
public abstract class EventCard extends TribeCard {

    /**Attribute to define the type of the event card*/
    private final EventType type;
    /**Attribute to define the two final cards, which are the SustenanceEvent and the ShamanicRitualEvent,
     to place at the bottom of the deck*/
    private final boolean isFinal;

    @Override
    public EventCard getAsEventCard() {
        return this;
    }

    /**General constructor for all event cards
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param type attribute to define the type of the event card
     * @param isFinal attribute to define the two final cards
     */
    public EventCard(Era era, int playersRequired, EventType type, boolean isFinal) {

        super(era, playersRequired);
        this.type=type;
        this.isFinal=isFinal;
    }

    /**Abstract method that will be implemented in each subclass with the behavior of each event card
     *
     * @param game it is used to get the list of players whose food and prestige points will be affected
     */
    public abstract void resolve(Game game);

    /**Getter method to obtain the type of the event card
     *
     * @return the type of the event card
     */
    public EventType getType() {
        return type; }

    /**Method to check if the event card is final and should be placed at the bottom of the deck
     *
     * @return true if it is one of the two final cards
     */
    public boolean isFinal() {
        return isFinal;
    }
}

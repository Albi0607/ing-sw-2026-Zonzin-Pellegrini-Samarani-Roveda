package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

/**Concrete class for cards of type Gatherer
 * @author Alberto Roveda
 */
public class Gatherer extends CharacterCard {

    /**Constructor for Gatherer Card
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     */
    public Gatherer(Era era, int playersRequired) {
        super(era, playersRequired, CharacterType.GATHERER);
    }

    /**Getter method to obtain the discount for the SustenanceEvent
     *
     * @return discount for the SustenanceEvent
     */
    public int getFoodDiscount() {
        return 3;
    }

}

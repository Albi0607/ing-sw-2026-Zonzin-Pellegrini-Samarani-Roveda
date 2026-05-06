package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.Era;

/**Concrete class for cards of type Gatherer
 * @author Alberto Roveda
 */
public class Gatherer extends CharacterCard {

    private final int discount;

    /**Constructor for Gatherer Card
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param discount the number of food items not to be paid for in SustenanceEvent
     */
    public Gatherer(Era era, int playersRequired,int discount) {
        super(era, playersRequired, CharacterType.GATHERER);
        this.discount=discount;
    }

    /**Getter method to obtain the discount for the SustenanceEvent
     *
     * @return discount for the SustenanceEvent
     */
    public int getFoodDiscount() {
        return discount;
    }

}

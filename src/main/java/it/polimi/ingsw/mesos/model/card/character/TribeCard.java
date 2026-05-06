package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.common.enums.Era;

/**Abstract class for all tribe cards
 * @author Alberto Roveda*/
public abstract class TribeCard extends Card {

    /**Attribute to indicate whether the card is used in the game based on the number of players*/
    private final int playersRequired;

    /**General constructor for all TribeCards
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game*/
    public TribeCard(Era era, int playersRequired) {

        super(era);
        this.playersRequired=playersRequired;
    }

    /**Getter method to obtain the minimum number of players required to use the card
     * @return the minimum number of player required
     */
    public int getPlayerRequired(){
        return this.playersRequired;
    }

}

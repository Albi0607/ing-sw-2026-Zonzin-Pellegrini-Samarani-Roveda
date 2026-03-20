package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.InventionIcon;

/**Concrete class for cards of type Inventor
 * @author Alberto Roveda
 */
public class Inventor extends CharacterCard {

    /**Attribute that distinguishes different Inventor cards to gain extra resources in case of diversity or equality*/
    private final InventionIcon icon;

    /**Constructor for Inventor Card
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param icon Attribute that distinguishes different Inventor cards
     */
    public Inventor(Era era, int playersRequired, InventionIcon icon) {
        super(era, playersRequired, CharacterType.INVENTOR);
        this.icon=icon;
    }

    /**Getter method to obtain the Inventor's icon
     *
     * @return Attribute that distinguishes different Inventor cards
     */
    public InventionIcon getIcon() {
        return icon;
    }

}

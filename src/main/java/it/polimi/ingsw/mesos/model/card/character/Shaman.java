package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

/**Concrete class for cards of type Shaman
 * @author Alberto Roveda
 */
public class Shaman extends CharacterCard {

    /**Attribute ranging from 1 to 3 to define the number of Shaman icons to use in ShamanicRitualEvent*/
    private final  int numberOfIcons;

    /**Constructor for Shaman Card
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param numberOfIcons define the number of Shaman icons
     */
    public Shaman(Era era, int playersRequired, int numberOfIcons) {
        super(era, playersRequired, CharacterType.SHAMAN);
        this.numberOfIcons=numberOfIcons;
    }

    /**Getter method to obtain the number of Shaman icons
     *
     * @return Shaman icons
     */

    public int getNumberOfIcons() {
        return numberOfIcons;
    }
}

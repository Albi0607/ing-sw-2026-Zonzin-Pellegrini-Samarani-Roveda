package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.Era;

/**Concrete class for cards of type Artist
 * @author Alberto Roveda
 */
public class Artist extends CharacterCard {

    /**Constructor for Artist cards
     *
     * @param era the era of the card
     * @param playersRequired the number of player required to use the card in the game
     */
    public Artist(Era era, int playersRequired) {
        super(era, playersRequired, CharacterType.ARTIST);
    }
}

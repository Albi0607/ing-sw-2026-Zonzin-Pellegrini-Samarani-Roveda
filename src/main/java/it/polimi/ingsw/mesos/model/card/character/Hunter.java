package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

/**Concrete class for cards of type Hunter
 * @author Alberto Roveda
 */
public class Hunter extends CharacterCard {

    /** If true, triggers immediate food gain when added to tribe */
    private final boolean hasIcon;

    /**Constructor for Hunter Card
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param hasIcon triggers immediate food gain when added to tribe if is true
     */
    public Hunter(Era era, int playersRequired, boolean hasIcon) {
        super(era, playersRequired, CharacterType.HUNTER);
        this.hasIcon=hasIcon;
    }

    /**
     * Called when the card is added to a tribe.
     * If hasIcon is true, player gains 1 food per Hunter already in the tribe (including this one)
     * @param tribe  tribe the tribe to which the card is added
     * @param player player the player who owns the tribe
     */

    public void onAddedToTribe(Tribe tribe, Player player) {
        if(hasIcon()){
            int bonus=tribe.countCharacters(CharacterType.HUNTER)+1;
            player.addFood(bonus);
        }
    }

    /**Getter method to check if the flag for gaining food when the card enters the tribe is true
     *
     * @return true if the card grants immediate food gain when added to a tribe
     */
    public boolean hasIcon() {
        return hasIcon;
    }
}

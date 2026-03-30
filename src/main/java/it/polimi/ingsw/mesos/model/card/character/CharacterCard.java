package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

/**Abstract class for all character cards
 * @author Alberto Roveda
 */

public abstract class CharacterCard extends TribeCard {

    /**Attribute specifying the type of the character card*/
    private final CharacterType type;

    /**General constructor for all character cards
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param type the type of the character card
     */
    public CharacterCard(Era era, int playersRequired, CharacterType type) {
        super(era, playersRequired);
        this.type=type;
    }

    /**Getter method to retrieve the type of the character card
     *
     * @return the type of the character card
     */
    public CharacterType getCharacterType(){
        return type;
    }

    public CharacterType getType() {
        return type;
    }

    @Override
    public void addTo(Player player) {
        // 1. Aggiungiamo il personaggio alla lista dei personaggi della tribù
        player.getTribe().addCharacter(this);
    }
}

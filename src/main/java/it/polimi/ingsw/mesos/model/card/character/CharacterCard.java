package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.Era;

/**Abstract class for all character cards
 */

public abstract class CharacterCard extends TribeCard {

    /**Attribute specifying the type of the character card*/
    private final CharacterType type;

    /**
     * General constructor for all character cards
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param type the type of the character card
     */
    public CharacterCard(Era era, int playersRequired, CharacterType type) {
        super(era, playersRequired);
        this.type=type;
    }

    /**
     * Getter method to retrieve the type of the character card
     *
     * @return the type of the character card
     */
    public CharacterType getCharacterType(){
        return type;
    }


    public CharacterType getType() {
        return type;
    }

    /**
     * Adds this character to the player's tribe.
     *
     * This method registers the character inside the player's tribe character list.
     *
     * @param player the player to which this character is added
     */
    @Override
    public void addTo(Player player) {
        // 1. Aggiungiamo il personaggio alla lista dei personaggi della tribù
        player.getTribe().addCharacter(this);
    }
}

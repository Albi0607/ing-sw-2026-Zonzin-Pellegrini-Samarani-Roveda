package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public abstract class CharacterCard extends TribeCard {

    private CharacterType type;

    public CharacterCard(Era era, int playersRequired, CharacterType type) {
        super(era, playersRequired);
    }

    /**
     * Called immediately when this card is added to the tribe.
     * Subclasses override this to implement their on-add effect.
     */
    //funzione utilizzata solo dagli hunter, non serve renderla generale si può implementarla solo nella classe
    //hunter ->alberto
    public void onAddedToTribe(Tribe tribe, Player player, Game game) { }

    public CharacterType getType() { return null; }
}

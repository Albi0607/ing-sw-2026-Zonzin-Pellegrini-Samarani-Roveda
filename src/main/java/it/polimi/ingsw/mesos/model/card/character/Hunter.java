package it.polimi.ingsw.mesos.model.card.character;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;

public class Hunter extends CharacterCard {

    /** If true, triggers immediate food gain when added to tribe. */
    private boolean hasIcon;

    public Hunter(Era era, int playersRequired, boolean hasIcon) {
        super(era, playersRequired, CharacterType.HUNTER);
    }

    /**
     * If hasIcon is true, player gains 1 food per Hunter already in the tribe
     * (including this one).
     */
    @Override
    public void onAddedToTribe(Tribe tribe, Player player, Game game) { }

    public boolean hasIcon() { return hasIcon; }
}

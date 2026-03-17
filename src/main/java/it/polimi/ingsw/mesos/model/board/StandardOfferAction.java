package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;

public class StandardOfferAction implements OfferAction {

    private int upperCount;
    private int lowerCount;
    private int foodBonus;
    /** If true, places totem in the first slot of TurnOrderTrack (tile A for 5p). */

    public StandardOfferAction(int upperCount, int lowerCount, int foodBonus) { }

    @Override
    public void execute(Player p, Game g) { }
}

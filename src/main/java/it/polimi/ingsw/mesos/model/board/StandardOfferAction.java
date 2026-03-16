package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;

public class StandardOfferAction extends OfferAction {

    private int upperCount;
    private int lowerCount;
    private int foodBonus;
    /** If true, places totem in the first slot of TurnOrderTrack (tile A for 5p). */
    //cosa serve questo metodo che utilità ha? ->alberto
    private boolean forceFirstPosition;

    public StandardOfferAction(int upperCount, int lowerCount, int foodBonus, boolean forceFirstPosition) { }

    @Override
    public void execute(Player p, Game g) { }
}

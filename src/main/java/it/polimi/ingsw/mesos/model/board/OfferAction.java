package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;

public abstract class OfferAction {

    public abstract void execute(Player p, Game g);
}

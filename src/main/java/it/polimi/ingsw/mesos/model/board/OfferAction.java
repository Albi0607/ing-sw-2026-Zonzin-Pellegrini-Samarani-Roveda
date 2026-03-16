package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
// l'ho cambiata ad interfaccia come da uml
public interface OfferAction {

    public abstract void execute(Player p, Game g);
}

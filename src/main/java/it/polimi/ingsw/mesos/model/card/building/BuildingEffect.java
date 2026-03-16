package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public abstract class BuildingEffect {
    //Non doveva essere un interfaccia? ->alberto
    public abstract void applyEffect(Player player, Game game, TriggerType trigger);
}

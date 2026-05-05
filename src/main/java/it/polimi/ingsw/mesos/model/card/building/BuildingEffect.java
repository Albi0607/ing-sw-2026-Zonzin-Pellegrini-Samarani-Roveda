package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.common.enums.TriggerType;
/**Interface used to manage the 14 different effects of the total 21 building cards*/
public interface BuildingEffect {
    /**Method that is specialized in the different subclasses to handle the effects of the buildings
     * @param player the player who benefits from the effect
     * @param game the game on which the building's effect acts
     * @param trigger the moment when the effect is triggered, to be evaluated with the TriggerType of the invoked building
     */
    public abstract void applyEffect(Player player, Game game, TriggerType trigger);
}

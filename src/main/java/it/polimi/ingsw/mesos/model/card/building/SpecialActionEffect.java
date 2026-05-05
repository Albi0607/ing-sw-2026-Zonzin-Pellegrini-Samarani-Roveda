package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.common.enums.SpecialActionType;
import it.polimi.ingsw.mesos.common.enums.TriggerType;

/**"Concrete class that handles the building effects:
 *
 * 4:At the end of your turn (including the last round), when you move your Totem onto the Turn Order track,
 * if you place it on a space that provides a food bonus, gain 1 additional food.
 * If you place it in the last space, you pay the food as usual and this building has no effect.
 * 13:In this and subsequent rounds, after resolving all actions (once all Totems have returned to the Turn Order track)
 * and before the End of Round phase, you may take 1 Character or Building card (by paying its cost) from the top row.
 * @author Alberto Roveda
 */

public class SpecialActionEffect implements BuildingEffect {

    /** Identifies which special behavior to apply: "FOOD_ON_TOTEM_SLOT", "EXTRA_DRAW"*/
    private final SpecialActionType specialType;

    /**Constructor for the effect of the 4 defined building types
     *
     * @param specialType Identifies which special behavior
     */
    public SpecialActionEffect(SpecialActionType specialType) {
        this.specialType=specialType;
    }

    /**Override of the interface method to handle the effects of the buildings
     * @param player the player who benefits from the effect
     * @param game the game on which the building's effect acts
     * @param trigger the moment when the effect is triggered, to be evaluated with the TriggerType of the invoked building
     */
    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if(trigger==TriggerType.ON_PURCHASE){

            /**Handles effect 4 by setting a flag in the player that manages the additional food gain*/
            if(specialType==SpecialActionType.FOOD_ON_TOTEM_SLOT){
                player.setFoodOnTotemSlot();
            }

            /**Handles effect 13 by setting a flag in the player that manages drawing an additional card from the top row*/
            if(specialType==SpecialActionType.EXTRA_DRAW){
                player.setExtraDraw();
            }
        }
    }
}

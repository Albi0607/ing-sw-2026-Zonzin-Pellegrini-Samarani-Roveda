package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.SpecialActionType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class SpecialActionEffect implements BuildingEffect {

    /**
     * Identifies which special behavior to apply.
     * Examples:"FOOD_ON_TOTEM_SLOT", "EXTRA_DRAW"
     */
    private final SpecialActionType specialType;

    public SpecialActionEffect(SpecialActionType specialType) {
        this.specialType=specialType;
    }


    //gestire questi effetti nello stato grazie agli attributi in player
    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if(trigger==TriggerType.ON_PURCHASE){

            if(specialType==SpecialActionType.FOOD_ON_TOTEM_SLOT){
                player.setFoodOnTotemSlot();
            }

            if(specialType==SpecialActionType.EXTRA_DRAW){
                player.setExtraDraw();
            }
        }
    }
}

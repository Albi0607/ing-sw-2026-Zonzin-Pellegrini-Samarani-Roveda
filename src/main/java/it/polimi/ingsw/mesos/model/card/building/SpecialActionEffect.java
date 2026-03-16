package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class SpecialActionEffect implements BuildingEffect {

    /**
     * Identifies which special behavior to apply.
     * Examples: "EXTRA_CARD_BEFORE_END_ROUND", "FOOD_ON_TOTEM_SLOT", "INVENTOR_PAIR_BONUS"
     */
    private String specialType;

    public SpecialActionEffect(String specialType) { }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) { }
}

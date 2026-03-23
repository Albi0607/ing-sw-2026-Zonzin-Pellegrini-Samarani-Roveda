package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class BuildingCard extends Card {

    private final int cost;
    private final int victoryPoints;
    private final BuildingEffect effect;

    public BuildingCard(Era era, int cost, int victoryPoints, BuildingEffect effect) {
        super(era);
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.effect = effect;
    }

    // --- Getters ---

    public int getCost() {
        return this.cost;
    }

    public int getVictoryPoints() {
        return this.victoryPoints;
    }

    public BuildingEffect getEffect() {
        return this.effect;
    }
}

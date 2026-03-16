package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class BuildingCard extends Card {

    private int cost;
    private int victoryPoints;
    private BuildingEffect effect;
    private EventType context;

    public BuildingCard(Era era, int cost, int victoryPoints, BuildingEffect effect, EventType context) {
        super(era);
    }

    @Override
    public boolean isPersistent() { return true; }

    @Override
    public boolean isBuilding() { return true; }

    // --- Getters ---

    public int getCost() { return 0; }

    public int getVictoryPoints() { return 0; }

    public BuildingEffect getEffect() { return null; }

    public EventType getContext() { return null; }
}

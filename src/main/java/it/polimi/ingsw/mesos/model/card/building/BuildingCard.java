package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class BuildingCard extends Card {

    /** Food cost to acquire (reduced by Builders in the tribe). */
    private int cost;

    /** Base prestige points printed on the card, awarded at end of game. */
    private int victoryPoints;

    /** The effect logic for this building. */
    private BuildingEffect effect;

    /**
     * Optional: the event context this building modifies (e.g. HUNT, SUSTENANCE).
     * Null if the building is not tied to a specific event.
     */
    private EventType context;

    public BuildingCard(Era era, int cost, int victoryPoints, BuildingEffect effect, EventType context) {
        super(era);
    }
    //Non dovrebbe essere utile poiché ci sono i triggerTyper e gli edifici non fanno azioni appena acquistati ->alberto
    /** Called once when the player acquires this building. */
    public void onAcquired(Player player, Game game) { }
    //è gia presente in buildingEffect non serve ->alberto
    /** Called by Game.notifyBuildingEffects for the matching trigger. */
    public void applyEffect(Player player, Game game, TriggerType trigger) { }

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

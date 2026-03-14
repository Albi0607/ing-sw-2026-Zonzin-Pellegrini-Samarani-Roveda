package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class SustenanceEvent extends EventCard {

    /** PP lost per unsatisfied character. Grows each Era. */
    private int prestigePoints;

    public SustenanceEvent(Era era, int playersRequired, int prestigePoints) {
        super(era, playersRequired, EventType.SUSTENANCE, false);
    }

    /**
     * Each player pays 1 food per character in their tribe.
     * For each unsatisfied character, they lose prestigePoints PP.
     * Gatherers provide a 3-food discount each.
     * Sustenance must always be resolved last among events.
     */
    @Override
    public void resolve(Game game) { }

    public int getPrestigePoints() { return 0; }
}

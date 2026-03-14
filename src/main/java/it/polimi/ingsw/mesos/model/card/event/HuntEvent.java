package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class HuntEvent extends EventCard {

    /** PP gained per Hunter in the tribe. Grows each Era. */
    private int prestigePoints;

    public HuntEvent(Era era, int playersRequired, int prestigePoints) {
        super(era, playersRequired, EventType.HUNT, false);
    }

    /**
     * Each player gains 1 food and prestigePoints PP
     * for each Hunter in their tribe.
     */
    @Override
    public void resolve(Game game) { }

    public int getPrestigePoints() { return 0; }
}

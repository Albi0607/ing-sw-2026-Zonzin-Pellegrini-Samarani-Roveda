package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class HuntEvent extends EventCard {

    private int prestigePoints;

    public HuntEvent(Era era, int playersRequired, int prestigePoints) {
        super(era, playersRequired, EventType.HUNT, false);
    }

    @Override
    public void resolve(Game game) { }
}

package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public abstract class EventCard extends TribeCard {

    private EventType type;
    private boolean isFinal;

    public EventCard(Era era, int playersRequired, EventType type, boolean isFinal) {
        super(era, playersRequired);
    }

    /** Resolve this event against all players in the game. */
    public abstract void resolve(Game game);

    public EventType getType() { return null; }

    public boolean isFinal() { return false; }
}

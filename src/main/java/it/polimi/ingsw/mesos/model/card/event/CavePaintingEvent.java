package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class CavePaintingEvent extends EventCard {

    private int loseNumber;
    private int gainNumber;
    private int losePoints;
    private int gainPoints;

    public CavePaintingEvent(Era era, int playersRequired,
                             int loseNumber, int gainNumber,
                             int losePoints, int gainPoints) {
        super(era, playersRequired, EventType.ROCK_ART, false);
    }

    /**
     * If a player has <= loseNumber Artists → loses losePoints PP.
     * If a player has >= gainNumber Artists → gains gainPoints PP per Artist.
     */
    @Override
    public void resolve(Game game) { }
}

package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class CavePaintingEvent extends EventCard {

    /** Minimum number of Artists below which a player loses PP. */
    private int loseNumber;

    /** Minimum number of Artists to gain PP per Artist. */
    private int gainNumber;

    /** PP lost if player has fewer or equal than loseNumber Artists. */
    private int losePoints;

    /** PP gained per Artist if player has at least gainNumber Artists. */
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

    public int getLoseNumber() { return 0; }

    public int getGainNumber() { return 0; }

    public int getLosePoints() { return 0; }

    public int getGainPoints() { return 0; }
}

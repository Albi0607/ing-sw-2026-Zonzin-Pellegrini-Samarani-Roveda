package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class ShamanicRitualEvent extends EventCard {

    /** PP gained by the player(s) with the most shaman icons. */
    private int gainPrestige;

    /** PP lost by the player(s) with the fewest shaman icons. */
    private int losePrestige;

    public ShamanicRitualEvent(Era era, int playersRequired, int gainPrestige, int losePrestige) {
        super(era, playersRequired, EventType.SHAMAN_RITUAL, false);
    }

    /**
     * Player with the most shaman icons gains gainPrestige PP.
     * Player with the fewest shaman icons loses losePrestige PP.
     * Ties: all tied players gain/lose.
     * Edge case: if all players are tied, all first gain then lose.
     */
    @Override
    public void resolve(Game game) { }

    public int getGainPrestige() { return 0; }

    public int getLosePrestige() { return 0; }
}

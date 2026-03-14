package it.polimi.ingsw.mesos.model.board;

import java.util.List;

public class OfferTrack {

    private List<OfferTile> tiles;

    public OfferTrack(int numPlayers) { }

    public List<OfferTile> getTiles() { return null; }

    public OfferTile getTile(char id) { return null; }

    /** Returns only tiles available for the current player count and without a totem. */
    public List<OfferTile> getAvailableTiles() { return null; }
}

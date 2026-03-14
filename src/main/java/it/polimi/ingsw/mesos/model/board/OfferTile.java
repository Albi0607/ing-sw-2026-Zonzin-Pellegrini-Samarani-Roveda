package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Totem;

public class OfferTile {

    private char id;
    private int minPlayers;
    private OfferAction action;
    private Totem hosts;

    public OfferTile(char id, int minPlayers, OfferAction action) { }

    public void placeTotem(Totem t) { }

    public void removeTotem() { }

    public boolean hasTotem() { return false; }

    public boolean isAvailableFor(int numPlayers) { return false; }

    public void executeAction(Player p, Game g) { }

    // --- Getters ---

    public char getId() { return 0; }

    public int getMinPlayers() { return 0; }

    public OfferAction getAction() { return null; }

    public Totem getHostedTotem() { return null; }
}

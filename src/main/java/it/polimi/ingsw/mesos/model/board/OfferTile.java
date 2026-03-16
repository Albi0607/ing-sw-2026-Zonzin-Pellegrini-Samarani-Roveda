package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Totem;

public class OfferTile {

    private char id;
    private int minPlayers;
    private OfferAction action;
    private Totem hosts;

    public OfferTile(char id, int minPlayers, OfferAction action) {
    }
/*
il metodo place/remove totem siamo sicuri di lasciarlo in game?
 */
}
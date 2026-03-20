package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.Color;

public class OfferTile {
    private int upperCount;
    private int lowerCount;
    private int foodBonus;
    private char id;
    private int minPlayers;
    private Player host;

    public OfferTile(char id, int minPlayers) {
    }

    public void execute(Player p, Game g) { }
/*
il metodo place/remove totem siamo sicuri di lasciarlo in game?
 */
}
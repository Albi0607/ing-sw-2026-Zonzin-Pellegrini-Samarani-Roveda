package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;

import java.util.List;

public class PlayerController {

    private Game game;

    public PlayerController(List<Game> games) {
    }

    /**
     * Places a player on a specific offer tile.
     *
     * @param player the totem to place
     * @param tile the tile where the totem will be placed
     */
    public void placeTotem(Player player, OfferTile tile) {
        if (player == null || tile == null) {
            throw new IllegalArgumentException("Totem or tile cannot be null");
        }

        tile.placeTotem(player);
    }

    /**
     * Allows the player to take a card.
     */
    // capire come gestire la row
    public void takeCard(int index, int row) {
        if (row == 1) {
            game.getBoard().takeCardFromUpper(index);
        } else {
            game.getBoard().takeCardFromLower(index);
        }
    }

    /**
     * Allows the player to buy a building.
     */
    public void buyBuilding() {
        // Logica da implementare
        System.out.println("Player buys a building");
    }
}
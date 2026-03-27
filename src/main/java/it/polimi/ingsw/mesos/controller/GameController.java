/**
 * Controller responsible for managing the lifecycle of a single game.
 *
 * It collects player nicknames from the view, validates them,
 * and creates a Game instance once the required number of players is reached.
 */

package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.Color;

import java.util.ArrayList;
import java.util.List;

/** The current active game */
public class GameController {

    private Game game;
    private List<String> nicknames;
    private int numPlayers;

    public GameController() {
        this.nicknames = new ArrayList<>();
    }

    /**
     * Sets the number of players required to start the game.
     *
     * @param numPlayers the number of players
     * @throws IllegalArgumentException if maxPlayers is less than or equal to 0 or grater than 5
     */
    public void setNumPlayers(int numPlayers) {
        if (numPlayers <= 0 || numPlayers >= 5) {
            throw new IllegalArgumentException("The number of players is not valid!");
        }
        this.numPlayers = numPlayers;
    }

    /**
     * Adds a player nickname.
     * When the required number of players is reached, the game is automatically created.
     *
     * @param nickname the player's nickname
     * @throws IllegalStateException if the game has already been created
     * @throws IllegalStateException if the maximum number of players is already reached
     * @throws IllegalArgumentException if the nickname is already used or invalid
     */
    public void addPlayer(String nickname) {
        if (game != null) {
            throw new IllegalStateException("Game has already been created");
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }

        if (nicknames.size() >= numPlayers) {
            throw new IllegalStateException("Maximum number of players reached");
        }

        // check nicknames
        if (nicknames.contains(nickname)) {
            throw new IllegalArgumentException("Nickname already in use");
        }
        // if the current nickname pass all the previous check --> it can be inserts in the list
        nicknames.add(nickname);

        // Automatically create the game when all players are added
        if (nicknames.size() == numPlayers) {
            createGame();
        }
    }

    /**
     * Creates the game using the collected nicknames.
     * Converts nicknames into Player objects.
     */
    private void createGame() {
        List<Player> players = new ArrayList<>();
        Color[] availableColors = Color.values();

        for (int i = 0; i < nicknames.size(); i++) {
            String nickname = nicknames.get(i);
            Color color = availableColors[i];

            players.add(new Player(nickname, color));
        }

        this.game = new Game(players);

        // Clear temporary data after game creation
        nicknames.clear();
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        if (game == null) {
            throw new IllegalStateException("Game not created");
        }
        game.startGame();
    }

    /**
     * Ends the latest game.
     */
    public void endGame() {
        // capire cosa mettere qui
        //  view.notifyGameEnded(game.getWinner());
    }

    /**
     * Updates the view (es. UI o client).
     */
    public void viewUpdate() {
    }

    /**
     * Updates the game board.
     */
    public void updateBoard() {
    }
}


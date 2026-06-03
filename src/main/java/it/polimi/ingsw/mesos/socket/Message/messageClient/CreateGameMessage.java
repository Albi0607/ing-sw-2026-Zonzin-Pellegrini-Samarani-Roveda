package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the client to request the creation of a new game.
 *
 * <p>Equivalent to {@code RemoteMethods.createNewGame(nickname, numPlayers, viewId)}.
 * The player's nickname and view ID are already known to the ClientHandler
 * and are therefore not included in this message.
 */
public class CreateGameMessage extends Message {
    private final int numPlayers;
    private final Color color;

    /**
     * Constructs a {@code CreateGameMessage} with the specified game settings.
     *
     * @param numPlayers the number of players expected to join the game
     * @param color      the color chosen by the creating player
     */
    public CreateGameMessage(int numPlayers, Color color) {
        this.numPlayers = numPlayers;
        this.color = color;
    }

    public int getNumPlayers() { return numPlayers; }
    public Color getColor() { return color; }
}

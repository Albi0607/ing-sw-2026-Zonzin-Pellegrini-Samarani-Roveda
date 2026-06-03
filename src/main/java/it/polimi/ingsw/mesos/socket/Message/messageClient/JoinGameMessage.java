package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the client to join an existing game.
 *
 * <p>Equivalent to {@code RemoteMethods.joinGame(nickname, id, viewId)}.
 * The player's nickname and view ID are already known to the ClientHandler
 * and are therefore not included in this message.
 */

public class JoinGameMessage extends Message {
    private final int gameId;
    private final Color color;

    /**
     * Constructs a {@code JoinGameMessage} with the specified game ID and color.
     *
     * @param gameId the identifier of the game to join
     * @param color  the color chosen by the joining player
     */
    public JoinGameMessage(int gameId, Color color) {
        this.gameId = gameId;
        this.color = color;
    }

    public int getGameId() { return gameId; }

    public Color getColor() { return color; }
}

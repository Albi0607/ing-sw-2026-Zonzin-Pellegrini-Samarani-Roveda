package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Inviato dal client per entrare in una partita esistente.
 * Equivale a RemoteMethods.joinGame(nickname, id, viewId).
 */
public class JoinGameMessage extends Message {
    private final int gameId;
    private final Color color;

    public JoinGameMessage(int gameId, Color color) {
        this.gameId = gameId;
        this.color = color;
    }

    public int getGameId() { return gameId; }

    public Color getColor() { return color; }
}

package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Inviato dal client per entrare in una partita esistente.
 * Equivale a RemoteMethods.joinGame(nickname, id, viewId).
 */
public class JoinGameMessage extends Message {
    private final int gameId;

    public JoinGameMessage(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() { return gameId; }
}

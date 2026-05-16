package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Inviato dal client per creare una nuova partita.
 * Equivale a RemoteMethods.createNewGame(nickname, numPlayers, viewId).
 * nickname e viewId sono già noti al ClientHandler, non servono nel messaggio.
 */
public class CreateGameMessage extends Message {
    private final int numPlayers;
    private final Color color;

    public CreateGameMessage(int numPlayers, Color color) {
        this.numPlayers = numPlayers;
        this.color = color;
    }

    public int getNumPlayers() { return numPlayers; }
    public Color getColor() { return color; }
}

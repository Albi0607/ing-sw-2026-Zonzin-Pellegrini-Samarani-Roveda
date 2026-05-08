package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Primo messaggio inviato dal client al server.
 * Equivale a RemoteMethods.getLobby(nickname, callback).
 * Registra il client nella lobby con il nickname scelto.
 */
public class GetLobbyMessage extends Message {
    private final String nickname;

    public GetLobbyMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() { return nickname; }
}

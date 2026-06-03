package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * First message sent by the client to the server upon connecting.
 *
 * <p>Equivalent to {@code RemoteMethods.getLobby(nickname, callback)}.
 * Registers the client in the lobby with the chosen nickname.
 */
public class GetLobbyMessage extends Message {
    private final String nickname;
    /**
     * Constructs a {@code GetLobbyMessage} with the specified nickname.
     *
     * @param nickname the nickname chosen by the connecting player
     */
    public GetLobbyMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() { return nickname; }
}

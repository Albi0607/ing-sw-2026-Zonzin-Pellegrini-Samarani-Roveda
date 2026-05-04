package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent from the client to the server to register a player nickname.
 * <p>
 * This message is typically used during the initial connection phase to identify
 * the player within the game session.
 * </p>
 */
public class RegisterMessage extends Message {
    private final String nickname;

    /**
     * Creates a new registration message containing the player's nickname.
     *
     * @param nickname the nickname chosen by the client
     */
    public RegisterMessage(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the nickname provided by the client.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }
}
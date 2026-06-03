package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent from a client to the server to request placing a totem
 * in a specific position.
 * <p>
 * When received by the server, it triggers the corresponding game logic
 * in the {@link GameController}.
 * </p>
 */
public class PlaceTotemMessage extends Message {
    private final String nickname;
    private final char position;

    /**
     * Creates a new request to place a totem.
     *
     * @param nickname the nickname of the player performing the action
     * @param position the position where the totem should be placed
     */
    public PlaceTotemMessage(String nickname, char position) {
        this.nickname = nickname;
        this.position = position;
    }

    /**
     * Executes the message on the server side by delegating the action
     * to the game controller.
     *
     * @param controller the game controller responsible for processing game logic
     */
    @Override
    public void executeServerSide(GameController controller) {
        controller.onPlaceTotem(nickname, position);
    }


    public String getNickname() { return nickname; }

    public char getPosition() { return position; }
}
package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientController;

/**
 * Message sent from the client to the server indicating that the player
 * chooses to skip an optional extra draw action.
 * <p>
 * This message is used when the game allows a player to either perform
 * or skip an additional draw phase, and the player explicitly decides to skip it.
 * </p>
 */
public class SkipExtraDrawMessage extends Message {

    private final String nickname;

    /**
     * Creates a message indicating that the specified player skips the extra draw.
     *
     * @param nickname the nickname of the player sending the request
     */
    public SkipExtraDrawMessage(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the nickname of the player who sent this message.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Executes the message on the server side.
     * <p>
     * Currently no logic is implemented. The server should handle the skip
     * action if required by the game rules.
     * </p>
     *
     * @param gameController the game controller managing game logic
     */
    @Override
    public void executeServerSide(GameController gameController) {
    }


}

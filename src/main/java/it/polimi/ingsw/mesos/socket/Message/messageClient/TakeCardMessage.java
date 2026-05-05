package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent from a client to the server to request taking a card
 * from a specific position.
 * <p>
 * The message also specifies whether the upper or lower card should be taken,
 * depending on the game rules.
 * </p>
 */
public class TakeCardMessage extends Message {
    private final String nickname;
    private final int position;
    private final boolean isUpper;

    /**
     * Creates a new request to take a card.
     *
     * @param nickname the nickname of the player performing the action
     * @param position the position of the card to take
     * @param isUpper true if the upper card should be taken, false otherwise
     */
    public TakeCardMessage(String nickname, int position, boolean isUpper) {
        this.nickname = nickname;
        this.position = position;
        this.isUpper = isUpper;
    }

    /**
     * Executes the message on the server side by delegating the action
     * to the game controller.
     *
     * @param controller the game controller responsible for game logic
     */
    @Override
    public void executeServerSide(GameController controller) {
        controller.onTakeCard(nickname, position, isUpper);
    }

    /**
     * Returns the nickname of the player who sent the request.
     *
     * @return the player's nickname
     */
    public String getNickname() { return nickname; }

    /**
     * Returns the position of the selected card.
     *
     * @return the card position
     */
    public int getPosition() { return position; }

    /**
     * Indicates whether the card selected is in the upper row.
     *
     * @return true if the card is in the upper row, false otherwise
     */
    public boolean isUpper() { return isUpper; }
}

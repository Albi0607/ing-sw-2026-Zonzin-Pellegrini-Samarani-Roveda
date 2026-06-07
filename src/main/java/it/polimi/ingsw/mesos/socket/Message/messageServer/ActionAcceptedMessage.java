package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the server to confirm that an action has been accepted.
 */
public class ActionAcceptedMessage extends Message {

    private final String message;

    /**
     * Constructs an ActionAcceptedMessage.
     *
     * @param message the confirmation message
     */
    public ActionAcceptedMessage(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.showActionAccepted(message);
    }

    /**
     * Returns the confirmation message.
     *
     * @return the message string
     */
    public String getMessage() {
        return message;
    }
}
package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the server to a specific client that has performed an invalid action.
 * This message is not broadcasted.
 */
public class ErrorMessage extends Message {

    private final String errorText;

    /**
     * Constructs an ErrorMessage.
     *
     * @param errorText the error description
     */
    public ErrorMessage(String errorText) {
        this.errorText = errorText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.showError(errorText);
    }

    /**
     * Returns the error text.
     *
     * @return the error string
     */
    public String getErrorText() { return errorText; }
}
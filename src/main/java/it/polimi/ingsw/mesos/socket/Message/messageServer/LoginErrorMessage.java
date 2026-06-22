package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the server when a login error occurs.
 */
public class LoginErrorMessage extends Message {
    private final String message;

    /**
     * Constructs a LoginErrorMessage.
     *
     * @param message the login error message
     */
    public LoginErrorMessage(String message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.showLoginError(message);
    }

    /**
     * Returns the error message.
     *
     * @return the message string
     */
    public String getMessage() {
        return message;
    }
}

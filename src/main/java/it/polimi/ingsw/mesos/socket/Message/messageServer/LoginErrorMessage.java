package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class LoginErrorMessage extends Message {
    private final String message;

    public LoginErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.showLoginError(message);
    }

    public String getMessage() {
        return message;
    }
}

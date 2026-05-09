package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class ActionAcceptedMessage extends Message {

    private final String message;

    public ActionAcceptedMessage(String message) {
        this.message = message;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.showActionAccepted(message);
    }

    public String getMessage() {
        return message;
    }
}
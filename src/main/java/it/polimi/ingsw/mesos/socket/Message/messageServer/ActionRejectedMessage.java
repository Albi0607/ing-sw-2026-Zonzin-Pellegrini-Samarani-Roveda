package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class ActionRejectedMessage extends Message {

    private final String reason;

    public ActionRejectedMessage(String reason) {
        this.reason = reason;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.showActionRejected(reason);
    }

    public String getReason() {
        return reason;
    }
}

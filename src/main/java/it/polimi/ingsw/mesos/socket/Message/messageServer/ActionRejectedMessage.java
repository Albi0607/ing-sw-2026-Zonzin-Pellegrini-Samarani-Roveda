package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the server to notify that an action has been rejected.
 */
public class ActionRejectedMessage extends Message {

    private final String reason;

    /**
     * Constructs an ActionRejectedMessage.
     *
     * @param reason the reason for the rejection
     */
    public ActionRejectedMessage(String reason) {
        this.reason = reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.showActionRejected(reason);
    }

    /**
     * Returns the reason for the rejection.
     *
     * @return the reason string
     */
    public String getReason() {
        return reason;
    }
}

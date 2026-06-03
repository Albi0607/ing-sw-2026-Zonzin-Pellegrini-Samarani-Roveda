package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;
import it.polimi.ingsw.mesos.controller.GameController;

/**
 * Keep-alive message sent periodically by the client to signal that the connection is still active.
 *
 * <p>When received by the server, no action is taken — its sole purpose is to
 * prevent the server's socket timeout from triggering a disconnection.

 */
public class PingMessage extends Message {
    private static final long serialVersionUID = 1L;

    @Override
    public void executeServerSide(GameController controller) {
    }
}

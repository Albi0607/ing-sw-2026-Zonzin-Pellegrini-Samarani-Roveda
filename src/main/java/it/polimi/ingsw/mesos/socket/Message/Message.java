package it.polimi.ingsw.mesos.socket.Message;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.network.ClientController;

import java.io.Serializable;

/**
 * Base abstract class for all messages exchanged between client and server.
 * <p>
 * Each message can be executed either on the client side or the server side
 * through the {@code executeClientSide} and {@code executeServerSide} methods.
 * Subclasses should override the appropriate method to define the specific
 * behavior of the message.
 * </p>
 */
public abstract class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Method invoked on the client side when the message is received from the server.
     * <p>
     * Subclasses may override this method to define how the message should be
     * handled on the client.
     * </p>
     *
     * @param controller the client controller responsible for application logic
     */
    public void executeClientSide(ClientController controller) {
        // default: non fa nulla
    }

    /**
     * Method invoked on the server side when the message is received from a client.
     * <p>
     * Subclasses may override this method to define how the message should be
     * handled on the server.
     * </p>
     *
     * @param serverController the GameController responsible for game logic
     */
    public void executeServerSide(GameController serverController) {
    }
}

package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the server to update the client's state (e.g., lobby, in game).
 * Example: when all players have registered, the server sends IN_GAME to all,
 * and the game starts.
 */
public class ClientStateMessage extends Message {

    private final ClientState state;

    /**
     * Constructs a ClientStateMessage.
     *
     * @param state the new client state
     */
    public ClientStateMessage(ClientState state) {
        this.state = state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.updateClientState(state);
    }

    /**
     * Returns the client state.
     *
     * @return the state
     */
    public ClientState getState() { return state; }
}

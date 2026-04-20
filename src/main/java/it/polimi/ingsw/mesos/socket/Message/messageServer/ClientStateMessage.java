package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.RMI.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Inviato dal server per aggiornare lo stato del client (lobby, in gioco, ecc.).
 * Esempio: quando tutti i giocatori si sono registrati, il server manda
 * IN_GAME a tutti e START_GAME diventa disponibile.
 */
public class ClientStateMessage extends Message {

    private final ClientState state;

    public ClientStateMessage(ClientState state) {
        this.state = state;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.updateClientState(state);
    }

    public ClientState getState() { return state; }
}

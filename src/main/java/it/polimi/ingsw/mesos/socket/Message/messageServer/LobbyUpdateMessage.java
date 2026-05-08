package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.socket.Message.Message;

import java.util.List;

/**
 * Inviato dal server a tutti i viewer della lobby quando la lista partite cambia.
 */
public class LobbyUpdateMessage extends Message {
    private final List<LobbyInfoDTO> lobby;

    public LobbyUpdateMessage(List<LobbyInfoDTO> lobby) {
        this.lobby = lobby;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.showLobby(lobby);
    }

    public List<LobbyInfoDTO> getLobby() { return lobby; }
}
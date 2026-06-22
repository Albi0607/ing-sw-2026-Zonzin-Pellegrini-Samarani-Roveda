package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.socket.Message.Message;

import java.util.List;

/**
 * Message sent by the server to all lobby viewers when the list of games changes.
 */
public class LobbyUpdateMessage extends Message {
    private final List<LobbyInfoDTO> lobby;

    /**
     * Constructs a LobbyUpdateMessage.
     *
     * @param lobby the updated list of lobby information
     */
    public LobbyUpdateMessage(List<LobbyInfoDTO> lobby) {
        this.lobby = lobby;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.showLobby(lobby);
    }

    /**
     * Returns the updated lobby list.
     *
     * @return the list of lobby info DTOs
     */
    public List<LobbyInfoDTO> getLobby() { return lobby; }
}
package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.util.List;

/**
 * Generic interface for managing how the client views the game representation. This interface will be implemented by
 * both CLI and GUI and is used by the ClientController in a generic way, so it does not need to be concerned with which
 * type of view has been implemented
 */
public interface View {

    /**
     * Method that allows the client to display the game updated to reflect the latest changes from the model
     * @param game latest game update
     */
    void showLastUpdate(GameDTO game);

    /**
     * Method that updates the attribute in the ClientController indicating the user's current state and, consequently,
     * the actions they must perform (mostly before the start of the game)
     * @param state latest updated client state
     */
    void showClientStateUpdate(ClientState state);

    /**
     * Method that allows the client to display error or general messages related to the connection and game progress
     * @param message message to be displayed in the client view
     */
    void showMessage(String message);

    //metodo per mostrare la lobby a schermo
    void showLobby(List<LobbyInfoDTO> lobby);

    void showActionRejected(String reason);

    void showActionAccepted(String message);

    void showLoginError(String message);
}

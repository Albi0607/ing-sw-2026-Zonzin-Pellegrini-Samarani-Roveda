package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.util.List;

/**
 * Generic interface used and managed by the server to send communications to the client without knowing which type of
 * network implementation is being used. The methods of this interface are invoked by the server in the same way for
 * both RMI and socket implementations. Two classes implement this interface, each handling these method calls according
 * to the specific network type
 */
public interface VirtualView {

    /**
     * Method that sends the game updated to the latest modification to the ClientController so that it can be displayed
     * in the client view
     * @param game latest game update
     */
    void sendGame(GameDTO game);

    /**
     * Method that sends the current state of the player (essential before the game starts) so that the client can
     * determine what actions it must perform
     * @param state latest updated client state
     */
    void sendClientState(ClientState state);

    /**
     * Method that allows the server to send error messages or general notifications to the client
     * @param message message to be displayed in the client view
     */
    void showMessage(String message);

    /**
     * Method used to retrieve the client's name
     * @return client nickname
     */
    String getNickname();


    //metodo per mandare la lobby in caso di modifiche
    void sendLobby(List<LobbyInfoDTO> lobby);

    String getId();

    void showActionRejected(String reason);
    void showActionAccepted(String message);

    void showLoginError(String message);

    void showLeaderboard(List<GameResult> leaderboard, int myPosition);

}

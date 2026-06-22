package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface that allows the server to invoke methods on the client to update the view and display messages
 */

public interface CallBack extends Remote {

    /**
     * Updates the client-side game state to reflect the latest changes from the server.
     *
     * @param game updated game state
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void updateGame(GameDTO game) throws RemoteException;

    /**
     * Updates the client state to determine the next required action.
     *
     * @param state current client state
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void updateClientState(ClientState state) throws RemoteException;

    /**
     * Sends a general message to be displayed on the client side.
     *
     * @param message message to display
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void showMessage(String message) throws RemoteException;

    /**
     * Sends the updated lobby state to the client.
     *
     * @param lobby list of available games in the lobby
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void showLobby(List<LobbyInfoDTO> lobby) throws RemoteException;

    /**
     * Notifies the client that an action was rejected by the server.
     *
     * @param reason explanation of why the action was rejected
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void showActionRejected(String reason) throws RemoteException;

    /**
     * Notifies the client that an action was successfully accepted by the server.
     *
     * @param message confirmation message
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void showActionAccepted(String message) throws RemoteException;

    /**
     * Notifies the client of a login error.
     *
     * @param message error message related to login
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void showLoginError(String message) throws RemoteException;

    /**
     * Shows the final leaderboard to the client at the end of a game session.
     * This method is invoked only when the server database is active, and the
     * leaderboard reflects both players from the finished match and the global
     * database ranking.
     *
     * @param leaderboard list of ranked players
     * @param myPosition final position of the current player
     * @throws RemoteException if a network error occurs during remote invocation
     */
    void showLeaderboard(List<GameResult> leaderboard, int myPosition) throws RemoteException;

}

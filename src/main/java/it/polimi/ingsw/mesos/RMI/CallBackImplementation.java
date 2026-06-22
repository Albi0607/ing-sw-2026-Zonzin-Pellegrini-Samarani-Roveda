package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Implementation of the CallBack remote interface.
 *
 * This class acts as the bridge between the RMI server and the local
 * ClientController. It receives remote calls from the server and forwards
 * them to the appropriate client-side logic, updating the view or state.
 */
public class CallBackImplementation extends UnicastRemoteObject implements CallBack {

    /**
     * Reference to the client controller used to update the game state and propagate messages to the user interface.
     */
    private final ClientController cController;


    /**
     * Constructs a new CallBackImplementation linked to the given client controller.
     *
     * @param controller the client controller used to handle updates and messages
     * @throws RemoteException if a network error occurs during remote object export
     */
    public CallBackImplementation(ClientController controller) throws RemoteException{
        this.cController=controller;
    }

    /**
     * Updates the client state to determine the next required action.
     *
     * @param state current client state
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void updateClientState(ClientState state) throws RemoteException {
        cController.updateClientState(state);
    }

    /**
     * Updates the client-side game state to reflect the latest changes from the server.
     *
     * @param game updated game state
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void updateGame(GameDTO game) throws RemoteException{
        cController.updateGame(game);
        System.out.println("Lato client: update game");
    };

    /**
     * Sends the updated lobby state to the client.
     *
     * @param lobby list of available games in the lobby
     * @throws RemoteException if a network error occurs during remote invocation
     */
    public void showLobby(List<LobbyInfoDTO> lobby) throws RemoteException{
        cController.showLobby(lobby);
    }

    /**
     * Sends a general message to be displayed on the client side.
     *
     * @param message message to display
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void showMessage(String message) throws RemoteException{
        cController.showMessage(message);
    }

    /**
     * Notifies the client that an action was rejected by the server.
     *
     * @param reason explanation of why the action was rejected
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void showActionRejected(String reason) throws RemoteException {
        cController.showActionRejected(reason);
    }

    /**
     * Notifies the client that an action was successfully accepted by the server.
     *
     * @param message confirmation message
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void showActionAccepted(String message) throws RemoteException {
        cController.showActionAccepted(message);
    }

    /**
     * Notifies the client of a login error.
     *
     * @param message error message related to login
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void showLoginError(String message) throws RemoteException {
        cController.showLoginError(message);
    }

    /**
     * Shows the final leaderboard to the client at the end of a game session.
     *This method is invoked only when the server database is active, and the
     * leaderboard reflects both players from the finished match and the global
     * database ranking.
     *
     * @param leaderboard list of ranked players
     * @param myPosition final position of the current player
     * @throws RemoteException if a network error occurs during remote invocation
     */
    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) throws RemoteException {
        cController.showLeaderboard(leaderboard, myPosition);
    }

}

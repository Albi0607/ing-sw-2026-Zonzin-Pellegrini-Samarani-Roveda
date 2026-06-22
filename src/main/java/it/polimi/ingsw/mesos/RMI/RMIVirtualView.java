package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.network.VirtualView;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * RMI implementation of the VirtualView interface.
 *
 * This class forwards server updates and notifications to a remote client
 * through RMI callbacks, providing a network-independent communication layer.
 */
public class RMIVirtualView implements VirtualView {

    /**
     * Nickname of the associated client.
     */
    private final String nickname;
    /**
     * Remote callback used by the server to invoke methods on the client side.
     */
    private final CallBack clientCallBack;
    /**
     * Unique identifier of this virtual connection.
     */
    private final String id;

    /**
     * Creates a new RMI-based VirtualView associated with a client.
     *
     * The instance is identified by a unique identifier and uses the provided
     * remote callback to allow the server to invoke methods on the client side.
     *
     * @param nickname the client's nickname
     * @param clientCallBack remote callback used by the server to communicate with the client
     */
    public RMIVirtualView(String nickname,CallBack clientCallBack){
        this.nickname=nickname;
        this.clientCallBack=clientCallBack;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Sends the updated game state to the client.
     *
     * The update is forwarded to the client-side controller through the remote callback.
     *
     * @param game the updated game state
     */
    @Override
    public void sendGame(GameDTO game) {
        try {
            clientCallBack.updateGame(game);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in sendGame");
        }
    }

    /**
     * Sends the current client state to the client.
     *
     * This information is used before the game starts to determine the available actions for the player.
     *
     * @param state the updated client state
     */
    @Override
    public void sendClientState(ClientState state){
        try {
            clientCallBack.updateClientState(state);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in sendClientState");
        }

    }

    /**
     * Sends the updated lobby state to the client.
     *
     * This method is invoked whenever the lobby changes and allows the client
     * to display the current list of available game sessions.
     *
     * @param lobby the updated lobby information
     */
    public void sendLobby(List<LobbyInfoDTO> lobby){
        try {
            clientCallBack.showLobby(lobby);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showMessage");
        }
    }

    /**
     * Sends a generic message or notification to the client.
     *
     * This method is used for generic informational messages
     *
     * @param message the message to display on the client side
     */
    @Override
    public void showMessage(String message) {
        try {
            clientCallBack.showMessage(message);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showMessage");
        }

    }

    /**
     * Notifies the client that a requested action has been rejected.
     *
     * @param reason the reason for the rejection
     */
    @Override
    public void showActionRejected(String reason) {
        try {
            clientCallBack.showActionRejected(reason);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showActionRejected: " + e.getMessage());
        }
    }

    /**
     * Notifies the client that a requested action has been successfully completed.
     *
     * @param message confirmation message for the completed action
     */
    @Override
    public void showActionAccepted(String message) {
        try {
            clientCallBack.showActionAccepted(message);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showActionAccepted: " + e.getMessage());
        }
    }

    /**
     * Notifies the client that the login attempt has failed.
     *
     * @param message the reason for the login failure
     */
    @Override
    public void showLoginError(String message) {
        try {
            clientCallBack.showLoginError(message);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showLoginError: " + e.getMessage());
        }
    }

    /**
     * Returns the nickname associated with the client.
     *
     * @return the client's nickname
     */
    @Override
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Returns the unique identifier of this virtual connection.
     *
     * @return the connection ID
     */
    @Override
    public String getId(){
        return id;
    }

    /**
     * Sends the global leaderboard to the client.
     *
     * The leaderboard represents the ranking of all players stored in the server database,
     * including players who have just completed a game.
     *
     * @param leaderboard the global ranking of all players
     * @param myPosition the position of the current player in the ranking
     */
    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        try {
            clientCallBack.showLeaderboard(leaderboard, myPosition);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showLeaderboard: " + e.getMessage());
        }
    }
}

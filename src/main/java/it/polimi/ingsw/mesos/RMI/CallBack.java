package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface that allows the server to invoke methods on the client to update the view and display messages
 */

public interface CallBack extends Remote {

    /**
     * Method that updates the client-side game state to reflect the latest updates and changes from the server
     * @param game updated to the latest change
     * @throws RemoteException if there are network errors during the method invocation
     */
    void updateGame(GameDTO game) throws RemoteException;

    /**
     * Method that updates the client state to determine the next required action
     * @param state current state of the client
     * @throws RemoteException if there are network errors during the method invocation
     */
    void updateClientState(ClientState state) throws RemoteException;

    /**
     * Method that allows messages sent by the server to be displayed on the client side
     * @param message message to be displayed
     * @throws RemoteException if there are network errors during the method invocation
     */
    void showMessage(String message) throws RemoteException;

    //metodo per mandare la lobby in caso di modifiche
    void showLobby(List<LobbyInfoDTO> lobby) throws RemoteException;

    void showActionRejected(String reason) throws RemoteException;
    void showActionAccepted(String message) throws RemoteException;
    void showLoginError(String message) throws RemoteException;
}
